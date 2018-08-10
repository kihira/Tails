package uk.kihira.tails.common;

public interface IDisposable {
    /**
     * This method handles cleaning up any resources that are not removed by normal GC means.
     * This can include OpenGL buffers and textures
     */
    void dispose();
}
