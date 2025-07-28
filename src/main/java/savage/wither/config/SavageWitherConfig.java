package savage.wither.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class SavageWitherConfig {
        public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
        public static final ForgeConfigSpec SPEC;
        public static final ForgeConfigSpec.DoubleValue WITHER_DAMAGE_MULTIPLIER;
        public static final ForgeConfigSpec.DoubleValue WITHER_HEALTH_MULTIPLIER;
        public static final ForgeConfigSpec.DoubleValue WITHER_SPEED_MULTIPLIER;
        public static final ForgeConfigSpec.DoubleValue WITHER_ARMOR_MULTIPLIER;
        public static final ForgeConfigSpec.DoubleValue WITHER_SKULL_SHOOT_RATE_MULTIPLIER;
        public static final ForgeConfigSpec.DoubleValue BLACK_SKULL_DAMAGE_MULTIPLIER;
        public static final ForgeConfigSpec.DoubleValue BLACK_SKULL_SPEED_MULTIPLIER;
        public static final ForgeConfigSpec.DoubleValue BLUE_SKULL_DAMAGE_MULTIPLIER;
        public static final ForgeConfigSpec.DoubleValue BLUE_SKULL_SPEED_MULTIPLIER;
        public static final ForgeConfigSpec.BooleanValue ENABLE_PREDICTIVE_AIM;
        public static final ForgeConfigSpec.IntValue PREDICTIVE_AIM_FREQUENCY;
        public static final ForgeConfigSpec.DoubleValue PREDICTIVE_AIM_ACCURACY;

        static {
                BUILDER.push("凋零基础属性");

                WITHER_DAMAGE_MULTIPLIER = BUILDER
                                .comment("凋零近战攻击伤害倍数")
                                .defineInRange("wither_damage_multiplier", 1.0, 0.1, Double.MAX_VALUE);

                WITHER_HEALTH_MULTIPLIER = BUILDER
                                .comment("凋零最大生命值倍数")
                                .defineInRange("wither_health_multiplier", 1.0, 0.1, Double.MAX_VALUE);

                WITHER_SPEED_MULTIPLIER = BUILDER
                                .comment("凋零移速倍数")
                                .defineInRange("wither_speed_multiplier", 1.0, 0.1, Double.MAX_VALUE);

                WITHER_ARMOR_MULTIPLIER = BUILDER
                                .comment("凋零护甲值倍数")
                                .defineInRange("wither_armor_multiplier", 1.0, 0.1, Double.MAX_VALUE);

                BUILDER.pop();

                BUILDER.push("头颅发射配置");

                WITHER_SKULL_SHOOT_RATE_MULTIPLIER = BUILDER
                                .comment("凋零头颅发射频率倍数")
                                .defineInRange("wither_skull_shoot_rate_multiplier", 1.0, 0.1, Double.MAX_VALUE);

                BUILDER.pop();

                BUILDER.push("黑色头颅配置");

                BLACK_SKULL_DAMAGE_MULTIPLIER = BUILDER
                                .comment("黑色头颅伤害倍数")
                                .defineInRange("black_skull_damage_multiplier", 1.0, 0.1, Double.MAX_VALUE);

                BLACK_SKULL_SPEED_MULTIPLIER = BUILDER
                                .comment("黑色头颅飞行速度倍数")
                                .defineInRange("black_skull_speed_multiplier", 1.0, 0.1, Double.MAX_VALUE);

                BUILDER.pop();

                BUILDER.push("蓝色头颅配置");

                BLUE_SKULL_DAMAGE_MULTIPLIER = BUILDER
                                .comment("蓝色头颅伤害倍数")
                                .defineInRange("blue_skull_damage_multiplier", 1.0, 0.1, Double.MAX_VALUE);

                BLUE_SKULL_SPEED_MULTIPLIER = BUILDER
                                .comment("蓝色头颅飞行速度倍数")
                                .defineInRange("blue_skull_speed_multiplier", 1.0, 0.1, Double.MAX_VALUE);

                BUILDER.pop();

                BUILDER.push("AI预瞄攻击配置");

                ENABLE_PREDICTIVE_AIM = BUILDER
                                .comment("是否启用AI预瞄攻击系统")
                                .define("enable_predictive_aim", false);

                PREDICTIVE_AIM_FREQUENCY = BUILDER
                                .comment("预瞄攻击频率 (每多少tick执行1次，数值越小攻击越频繁)")
                                .defineInRange("predictive_aim_frequency", 15, 5, 100);

                PREDICTIVE_AIM_ACCURACY = BUILDER
                                .comment("预瞄攻击精度倍数 (数值越高越精准)")
                                .defineInRange("predictive_aim_accuracy", 2.0, 0.1, 100.0);

                BUILDER.pop();
                SPEC = BUILDER.build();
        }
}