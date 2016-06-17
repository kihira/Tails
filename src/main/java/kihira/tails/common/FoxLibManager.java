/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Zoe Lee (Kihira) and iLexiconn
 *
 * Some code used from CodeChickenCore under the MIT License
 *
 * See LICENSE for full License
 */

package kihira.tails.common;

import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.versioning.ComparableVersion;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;
import net.minecraftforge.fml.common.versioning.VersionParser;
import net.minecraftforge.fml.relauncher.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.CountingOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.URL;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@IFMLLoadingPlugin.MCVersion(value = FoxLibManager.MC_VERSION)
public class FoxLibManager implements IFMLCallHook, IFMLLoadingPlugin {

    private static final String foxlibVersion = "@FOXLIBVERSION@";
    private static final String foxlibReqVersion = "[0.10.0,)";
    private static final String foxlibFileName = "FoxLib-"+foxlibVersion+".jar";
    private static final String foxlibDownloadLink = "http://addons-origin.cursecdn.com/files/2307/728/FoxLib-1.9.4-0.10.0.jar"; // todo use own cdn
    private static final String foxlibDownloadFallback = "http://minecraft.curseforge.com/mc-mods/223291-kihira.foxlib/files";
    public static final Logger logger = LogManager.getLogger("FoxLib Manager");
    static final String MC_VERSION = "@MCVERSION@";
    private static final Pattern pattern = Pattern.compile("(\\w+)[-][\\d\\.]+.*?([\\d\\.]{5,})[\\w]*.*?\\.jar", Pattern.CASE_INSENSITIVE);

    private int totalSize;

    @Override
    public String[] getASMTransformerClass() {
        return null;
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return this.getClass().getName();
    }

    @Override
    public void injectData(Map<String, Object> data) {}

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    @Override
    public Void call() throws Exception {
        byte[] bs = Launch.classLoader.getClassBytes("net.minecraft.world.World");
        if (bs != null) {
            logger.warn("We are in a dev environment, skipping version check");
        }
        else {
            checkFoxLib();
        }

        return null;
    }

    private void checkFoxLib() {
        TreeMap<ComparableVersion, File> foxLibs = buildFoxLibFileList();

        //We have multiple versions, remove the old ones
        if (foxLibs.size() >= 2) {
            int i = 0;
            for (File file : foxLibs.values()) {
                if (i == 0) i++;
                else {
                    logger.info("Removing old version of FoxLib with the name " + file.getName());
                    if (!file.delete()) {
                        logger.error("Failed to delete file, removing on exit");
                        file.deleteOnExit();
                    }
                }
            }
        }
        //We have none, ask for download
        else if (foxLibs.size() == 0) {
            if (!GraphicsEnvironment.isHeadless()) {
                showDownloadOptionDialog("FoxLib is not installed and required! Would you like to download it?");
                checkFoxLib();
            }
            else {
                logger.error("FoxLib is REQUIRED to use Tails and must be downloaded in order to use the mod");
                System.exit(-1);
            }
        }
        //We have one, check it is the correct version
        else {
            if (!VersionParser.parseRange(foxlibReqVersion).containsVersion(new DefaultArtifactVersion(FoxLibManager.MC_VERSION + "-" + foxLibs.firstKey().toString()))) {
                if (!GraphicsEnvironment.isHeadless()) {
                    showDownloadOptionDialog("FoxLib is not the required version! Would you like to update it?");
                    checkFoxLib();
                }
                else {
                    logger.error("A newer version of FoxLib is REQUIRED to use Tails and must be downloaded in order to use the mod");
                    System.exit(-1);
                }
            }
        }
    }

    private TreeMap<ComparableVersion, File> buildFoxLibFileList() {
        File[] files = new File((File) FMLInjectionData.data()[6], "mods").listFiles();
        TreeMap<ComparableVersion, File> foxLibs = new TreeMap<ComparableVersion, File>(new Comparator<ComparableVersion>() {
            @Override
            public int compare(ComparableVersion o1, ComparableVersion o2) {
                return o2.compareTo(o1);
            }
        });

        //Compile file list
        findFoxlibFiles(foxLibs, files);
        File dir = new File((File) FMLInjectionData.data()[6], "mods" + File.separator + MC_VERSION);
        if (dir.exists()) {
            files = dir.listFiles();
            findFoxlibFiles(foxLibs, files);
        }

        return foxLibs;
    }

    private void findFoxlibFiles(TreeMap<ComparableVersion, File> map, File[] files) {
        for (File file : files) {
/*            if (file.isDirectory()) {
                findFoxlibFiles(map, file.listFiles());
            }*/
            Matcher matcher = pattern.matcher(file.getName());
            if (matcher.matches()) {
                String name = matcher.group(1);
                //Check we have the mod
                if (name.equalsIgnoreCase("FoxLib")) {
                    ComparableVersion version = new ComparableVersion(matcher.group(2));
                    map.put(version, file);
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void showDownloadOptionDialog(String message) {
        logger.info("Requesting users input for FoxLib. Check your windows!");
        logger.info(message);
        int opt = JOptionPane.showConfirmDialog(Display.getParent(), message);
        if (opt == JOptionPane.OK_OPTION) {
            File target = null;
            URL download;
            OutputStream output = null;
            InputStream input = null;
            try {
                File dir = new File((File) FMLInjectionData.data()[6], "mods" + File.separator + MC_VERSION);
                if (dir.exists()) {
                    logger.info("Detected MC specific sub directory, downloading to there instead of main directory");
                    target = new File(dir, File.separator + foxlibFileName);
                }
                else {
                    target = new File((File) FMLInjectionData.data()[6], "mods" + File.separator + foxlibFileName);
                }
                download = new URL(foxlibDownloadLink);
                output = new FileOutputStream(target);
                input = download.openStream();

                totalSize = Integer.valueOf(download.openConnection().getHeaderField("Content-Length"));

                final ProgressMonitor monitor = new ProgressMonitor(Display.getParent(), "Downloading FoxLib", null, 0, totalSize - 100);
                monitor.setMillisToPopup(0);
                final InputStream finalInput = input;
                final OutputStream finalOutput = output;
                new Runnable() {
                    @Override
                    public void run() {
                        DownloadCountingOutputStream countingOutputStream = new DownloadCountingOutputStream(finalOutput, monitor);
                        try {
                            IOUtils.copy(finalInput, countingOutputStream);

                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            monitor.close();
                        }
                    }
                }.run();

            } catch (IOException e) {
                //Delete file cause it could be corrupt
                if (!target.delete()) {
                    target.deleteOnExit();
                }
                e.printStackTrace();

                JOptionPane.showMessageDialog(Display.getParent(), "Failed to download FoxLib! Manually download from " + foxlibDownloadFallback, "Download Failed", JOptionPane.ERROR_MESSAGE);
                logger.error("FoxLib is REQUIRED to use Tails and must be downloaded in order to use the mod");
                FMLCommonHandler.instance().exitJava(-1, true);
            } finally {
                IOUtils.closeQuietly(output);
                IOUtils.closeQuietly(input);
            }
        }
        else if (opt == JOptionPane.CLOSED_OPTION || opt == JOptionPane.NO_OPTION) {
            logger.error("FoxLib is REQUIRED to use Tails and must be downloaded in order to use the mod");
            System.exit(-1);
        }
    }

    private class DownloadCountingOutputStream extends CountingOutputStream {

        private final ProgressMonitor update;

        DownloadCountingOutputStream(OutputStream out, ProgressMonitor update) {
            super(out);
            this.update = update;
        }

        @Override
        protected void afterWrite(int n) throws IOException {
            super.afterWrite(n);

            update.setProgress((int) getByteCount());
        }
    }
}