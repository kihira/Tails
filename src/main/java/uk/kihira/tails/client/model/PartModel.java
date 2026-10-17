package uk.kihira.tails.client.model;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector3f;
import uk.kihira.tails.Tails;
import uk.kihira.tails.client.render.LegacyLayerPart;

import java.util.Collections;
import java.util.function.Function;

// ties us to only rendering on players but should be fine
public abstract class PartModel extends HumanoidModel<AvatarRenderState>
{
    protected final ModelPart partRoot;

    public PartModel()
    {
        this(EMPTY_MODEL, RenderTypes::entityTranslucent);
    }

    public PartModel(ModelPart root)
    {
        this(root, RenderTypes::entityTranslucent);
    }

    public PartModel(ModelPart root, Function<Identifier, RenderType> renderType)
    {
        super(root, renderType);
        // As we don't know where partRoot might be (because we currently base on the HumanoidModel), we need to search for it
        var partRootParent = root.getAllParts().stream().filter(modelPart -> modelPart.hasChild(PART_ROOT_NAME)).findFirst();
        if (partRootParent.isPresent())
        {
            this.partRoot = partRootParent.get().getChild(PART_ROOT_NAME);
        }
        else
        {
            Tails.LOGGER.fatal("{} has no ModelPart called '{}'. This is likely an incorrect setup model", this.getClass().getSimpleName(), PART_ROOT_NAME);
            this.partRoot = root;
        }
    }

    @Override
    public void setupAnim(AvatarRenderState renderState)
    {
        super.setupAnim(renderState);

        var partDataQueue = renderState.getRenderData(LegacyLayerPart.PART_DATA_KEY);
        if (partDataQueue != null)
        {
            var partData = partDataQueue.poll();
            this.partRoot.offsetPos(partData.mountOffset);
            this.partRoot.offsetRotation(partData.rotation);
            // TODO: Part data should probably store the scale offset so we can avoid doing this
            // Pass in a "dest" vector3f so it doesn't override our part data
            this.partRoot.offsetScale(partData.scale.sub(new Vector3f(1f, 1f, 1f), new Vector3f()));

        }
    }

    protected static float getAnimationTime(double cycleTime, long offset)
    {
        //Returns between 0-360 in radians depending on far in the "cycle" we are.
        //TODO could probably use ageInTicks as an alternative in the future
        // This currently needs to have cycleTime as a double, most likely due to precision as otherwise the number effectively doesn't update
        return ((float)(((offset + System.currentTimeMillis()) % cycleTime) / cycleTime) * 2f * Mth.PI);
    }

    public static final String PART_ROOT_NAME = "partRoot";
    private static final ModelPart EMPTY_MODEL = new ModelPart(Collections.emptyList(), Collections.singletonMap(PART_ROOT_NAME, new ModelPart(Collections.emptyList(), Collections.emptyMap())));
}
