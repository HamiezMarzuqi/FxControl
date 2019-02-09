package mcjty.fxcontrol.rules;

import com.google.gson.JsonElement;
import mcjty.fxcontrol.FxControl;
import mcjty.fxcontrol.rules.support.GenericRuleEvaluator;
import mcjty.tools.rules.IEventQuery;
import mcjty.tools.rules.RuleBase;
import mcjty.tools.typed.Attribute;
import mcjty.tools.typed.AttributeMap;
import mcjty.tools.typed.GenericAttributeMapFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.function.Consumer;

import static mcjty.fxcontrol.rules.support.RuleKeys.*;

public class RightClickRule extends RuleBase<RuleBase.EventGetter> {

    private static final GenericAttributeMapFactory FACTORY = new GenericAttributeMapFactory();
    public static final IEventQuery<PlayerInteractEvent.RightClickBlock> EVENT_QUERY = new IEventQuery<PlayerInteractEvent.RightClickBlock>() {
        @Override
        public World getWorld(PlayerInteractEvent.RightClickBlock o) {
            return o.getWorld();
        }

        @Override
        public BlockPos getPos(PlayerInteractEvent.RightClickBlock o) {
            return o.getPos();
        }

        @Override
        public BlockPos getValidBlockPos(PlayerInteractEvent.RightClickBlock o) {
            return o.getPos();
        }

        @Override
        public int getY(PlayerInteractEvent.RightClickBlock o) {
            return o.getPos().getY();
        }

        @Override
        public Entity getEntity(PlayerInteractEvent.RightClickBlock o) {
            return o.getEntityPlayer();
        }

        @Override
        public DamageSource getSource(PlayerInteractEvent.RightClickBlock o) {
            return null;
        }

        @Override
        public Entity getAttacker(PlayerInteractEvent.RightClickBlock o) {
            return null;
        }

        @Override
        public EntityPlayer getPlayer(PlayerInteractEvent.RightClickBlock o) {
            return o.getEntityPlayer();
        }
    };

    static {
        FACTORY
                .attribute(Attribute.create(MINTIME))
                .attribute(Attribute.create(MAXTIME))
                .attribute(Attribute.create(MINLIGHT))
                .attribute(Attribute.create(MAXLIGHT))
                .attribute(Attribute.create(MINHEIGHT))
                .attribute(Attribute.create(MAXHEIGHT))
                .attribute(Attribute.create(MINDIFFICULTY))
                .attribute(Attribute.create(MAXDIFFICULTY))
                .attribute(Attribute.create(MINSPAWNDIST))
                .attribute(Attribute.create(MAXSPAWNDIST))
                .attribute(Attribute.create(RANDOM))
                .attribute(Attribute.create(SEESKY))
                .attribute(Attribute.create(WEATHER))
                .attribute(Attribute.create(TEMPCATEGORY))
                .attribute(Attribute.create(DIFFICULTY))
                .attribute(Attribute.create(STRUCTURE))

                .attribute(Attribute.create(GAMESTAGE))

                .attribute(Attribute.create(WINTER))
                .attribute(Attribute.create(SUMMER))
                .attribute(Attribute.create(SPRING))
                .attribute(Attribute.create(AUTUMN))

                .attribute(Attribute.create(INBUILDING))
                .attribute(Attribute.create(INCITY))
                .attribute(Attribute.create(INSTREET))
                .attribute(Attribute.create(INSPHERE))

                .attribute(Attribute.createMulti(AMULET))
                .attribute(Attribute.createMulti(RING))
                .attribute(Attribute.createMulti(BELT))
                .attribute(Attribute.createMulti(TRINKET))
                .attribute(Attribute.createMulti(HEAD))
                .attribute(Attribute.createMulti(BODY))
                .attribute(Attribute.createMulti(CHARM))

                .attribute(Attribute.createMulti(BLOCK))
                .attribute(Attribute.createMulti(BLOCKUP))
                .attribute(Attribute.createMulti(HELMET))
                .attribute(Attribute.createMulti(CHESTPLATE))
                .attribute(Attribute.createMulti(LEGGINGS))
                .attribute(Attribute.createMulti(BOOTS))
                .attribute(Attribute.createMulti(HELDITEM))
                .attribute(Attribute.createMulti(OFFHANDITEM))
                .attribute(Attribute.createMulti(BOTHHANDSITEM))
                .attribute(Attribute.createMulti(BIOME))
                .attribute(Attribute.createMulti(BIOMETYPE))
                .attribute(Attribute.createMulti(DIMENSION))

                .attribute(Attribute.createMulti(ACTION_POTION))
                .attribute(Attribute.create(ACTION_MESSAGE))
                .attribute(Attribute.create(ACTION_FIRE))
                .attribute(Attribute.create(ACTION_EXPLOSION))
                .attribute(Attribute.create(ACTION_CLEAR))
                .attribute(Attribute.create(ACTION_DAMAGE))
                .attribute(Attribute.create(ACTION_RESULT))
        ;
    }

    private Event.Result result;
    private final GenericRuleEvaluator ruleEvaluator;

    private RightClickRule(AttributeMap map) {
        super(FxControl.logger);
        ruleEvaluator = new GenericRuleEvaluator(map);
        addActions(map);
    }

    @Override
    protected void addActions(AttributeMap map) {
        super.addActions(map);

        if (map.has(ACTION_RESULT)) {
            String br = map.get(ACTION_RESULT);
            if ("default".equals(br) || br.startsWith("def")) {
                this.result = Event.Result.DEFAULT;
            } else if ("allow".equals(br) || "true".equals(br)) {
                this.result = Event.Result.ALLOW;
            } else {
                this.result = Event.Result.DENY;
            }
        } else {
            this.result = Event.Result.DEFAULT;
        }
    }

    public boolean match(PlayerInteractEvent.RightClickBlock event) {
        return ruleEvaluator.match(event, EVENT_QUERY);
    }

    public void action(PlayerInteractEvent.RightClickBlock event) {
        EventGetter getter = new EventGetter() {
            @Override
            public EntityLivingBase getEntityLiving() {
                return event.getEntityPlayer();
            }

            @Override
            public EntityPlayer getPlayer() {
                return event.getEntityPlayer();
            }

            @Override
            public World getWorld() {
                return event.getWorld();
            }

            @Override
            public BlockPos getPosition() {
                return event.getPos();
            }
        };
        for (Consumer<EventGetter> action : actions) {
            action.accept(getter);
        }
    }

    public Event.Result getResult() {
        return result;
    }


    public static RightClickRule parse(JsonElement element) {
        if (element == null) {
            return null;
        } else {
            AttributeMap map = null;
            try {
                map = FACTORY.parse(element);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return new RightClickRule(map);
        }
    }
}