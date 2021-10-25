package io.github.darkkronicle.advancedchathud.config;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigOptionList;
import fi.dy.masa.malilib.config.options.ConfigString;
import fi.dy.masa.malilib.util.StringUtils;
import io.github.darkkronicle.advancedchatcore.config.ConfigStorage;
import io.github.darkkronicle.advancedchatcore.config.options.ConfigSimpleColor;
import io.github.darkkronicle.advancedchatcore.interfaces.IJsonSave;
import io.github.darkkronicle.advancedchatcore.util.ColorUtil;
import io.github.darkkronicle.advancedchatcore.util.FindType;
import io.github.darkkronicle.advancedchathud.AdvancedChatHud;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Data;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Data
@Environment(EnvType.CLIENT)
public class ChatTab {

    private static String translate(String key) {
        return StringUtils.translate("advancedchathud.config.tab." + key);
    }

    private UUID uuid = UUID.randomUUID();

    private ConfigStorage.SaveableConfig<ConfigString> name = ConfigStorage.SaveableConfig.fromConfig(
        "name",
        new ConfigString(
            translate("name"),
            "Boring Chat Tab",
            translate("info.name")
        )
    );

    private ConfigStorage.SaveableConfig<ConfigString> findString = ConfigStorage.SaveableConfig.fromConfig(
        "findString",
        new ConfigString(
            translate("findstring"),
            "Divert this message!",
            translate("info.findstring")
        )
    );

    private ConfigStorage.SaveableConfig<ConfigOptionList> findType = ConfigStorage.SaveableConfig.fromConfig(
        "findType",
        new ConfigOptionList(
            translate("findtype"),
            FindType.LITERAL,
            translate("info.findtype")
        )
    );

    private ConfigStorage.SaveableConfig<ConfigString> startingMessage = ConfigStorage.SaveableConfig.fromConfig(
        "startingMessage",
        new ConfigString(
            translate("startingmessage"),
            "",
            translate("info.startingmessage")
        )
    );

    private ConfigStorage.SaveableConfig<ConfigBoolean> forward = ConfigStorage.SaveableConfig.fromConfig(
        "forward",
        new ConfigBoolean(translate("forward"), true, translate("info.forward"))
    );

    private ConfigStorage.SaveableConfig<ConfigString> abbreviation = ConfigStorage.SaveableConfig.fromConfig(
        "abbreviation",
        new ConfigString(
            translate("abbreviation"),
            "BCT",
            translate("info.abbreviation")
        )
    );

    private ConfigStorage.SaveableConfig<ConfigSimpleColor> mainColor = ConfigStorage.SaveableConfig.fromConfig(
        "mainColor",
        new ConfigSimpleColor(
            translate("maincolor"),
            ColorUtil.GRAY.withAlpha(100),
            translate("info.maincolor")
        )
    );

    private ConfigStorage.SaveableConfig<ConfigSimpleColor> borderColor = ConfigStorage.SaveableConfig.fromConfig(
        "borderColor",
        new ConfigSimpleColor(
            translate("bordercolor"),
            ColorUtil.BLACK.withAlpha(180),
            translate("info.bordercolor")
        )
    );

    private ConfigStorage.SaveableConfig<ConfigSimpleColor> innerColor = ConfigStorage.SaveableConfig.fromConfig(
        "innerColor",
        new ConfigSimpleColor(
            translate("innercolor"),
            ColorUtil.BLACK.withAlpha(100),
            translate("info.innercolor")
        )
    );

    private ConfigStorage.SaveableConfig<ConfigBoolean> showUnread = ConfigStorage.SaveableConfig.fromConfig(
        "showUnread",
        new ConfigBoolean(
            translate("showunread"),
            false,
            translate("info.showunread")
        )
    );

    private final ImmutableList<ConfigStorage.SaveableConfig<?>> options = ImmutableList.of(
        name,
        findString,
        findType,
        startingMessage,
        forward,
        abbreviation,
        mainColor,
        borderColor,
        innerColor,
        showUnread
    );

    /**
     * Options that the main tab can use
     */
    private final ImmutableList<ConfigStorage.SaveableConfig<?>> mainEditableOptions = ImmutableList.of(
        name,
        startingMessage,
        abbreviation,
        mainColor,
        borderColor,
        innerColor,
        showUnread
    );

    public FindType getFind() {
        return (FindType) findType.config.getOptionListValue();
    }

    public List<String> getWidgetHoverLines() {
        String translated = StringUtils.translate(
            "advancedchathud.config.tabdescription"
        );
        ArrayList<String> hover = new ArrayList<>();
        for (String s : translated.split("\n")) {
            hover.add(
                s
                    .replaceAll(
                        Pattern.quote("<name>"),
                        Matcher.quoteReplacement(name.config.getStringValue())
                    )
                    .replaceAll(
                        Pattern.quote("<starting>"),
                        Matcher.quoteReplacement(
                            startingMessage.config.getStringValue()
                        )
                    )
                    .replaceAll(
                        Pattern.quote("<forward>"),
                        Matcher.quoteReplacement(
                            forward.config.getStringValue()
                        )
                    )
                    .replaceAll(
                        Pattern.quote("<find>"),
                        Matcher.quoteReplacement(
                            findString.config.getStringValue()
                        )
                    )
                    .replaceAll(
                        Pattern.quote("<findtype>"),
                        Matcher.quoteReplacement(getFind().getDisplayName())
                    )
            );
        }
        return hover;
    }

    public static class ChatTabJsonSave implements IJsonSave<ChatTab> {

        @Override
        public ChatTab load(JsonObject obj) {
            ChatTab t = new ChatTab();
            for (ConfigStorage.SaveableConfig<?> conf : t.getOptions()) {
                IConfigBase option = conf.config;
                if (obj.has(conf.key)) {
                    option.setValueFromJsonElement(obj.get(conf.key));
                }
            }
            if (obj.has("uuid")) {
                try {
                    t.setUuid(UUID.fromString(obj.get("uuid").getAsString()));
                } catch (Exception e) {
                    // Failed, but a new one will happen
                    AdvancedChatHud.LOGGER.warn(
                        "Tab " +
                        t.getName().config.getStringValue() +
                        " did not have a UUID. New one will be generated."
                    );
                }
            }
            return t;
        }

        @Override
        public JsonObject save(ChatTab tab) {
            JsonObject obj = new JsonObject();
            for (ConfigStorage.SaveableConfig<?> option : tab.getOptions()) {
                obj.add(option.key, option.config.getAsJsonElement());
            }
            obj.addProperty("uuid", tab.getUuid().toString());
            return obj;
        }
    }

    public static ChatTab getMainOptions() {
        ChatTab tab = new ChatTab();
        tab.getName().config.setValueFromString("Main");
        tab.getAbbreviation().config.setValueFromString("Main");
        return tab;
    }
}