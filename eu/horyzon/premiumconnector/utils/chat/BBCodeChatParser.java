package eu.horyzon.premiumconnector.utils.chat;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class BBCodeChatParser implements ChatParser {
	private static final Pattern pattern = Pattern.compile("(?is)(?=\\n)|(?:[&§](?<color>[0-9A-FK-OR]))|(?:\\[(?<tag>/?(?:b|i|u|s|nocolor|nobbcode)|(?:url|command|hover|suggest|color)=(?<value>(?:(?:[^]\\[]*)\\[(?:[^]\\[]*)\\])*(?:[^]\\[]*))|/(?:url|command|hover|suggest|color))\\])|(?:\\[(?<implicitTag>url|command|suggest)\\](?=(?<implicitValue>.*?)\\[/\\k<implicitTag>\\]))");
	private static final Pattern strip_bbcode_pattern = Pattern.compile("(?is)(?:\\[(?<tag>/?(?:b|i|u|s|nocolor|nobbcode)|(?:url|command|hover|suggest|color)=(?<value>(?:(?:[^]\\[]*)\\[(?:[^]\\[]*)\\])*(?:[^]\\[]*))|/(?:url|command|hover|suggest|color))\\])|(?:\\[(?<implicitTag>url|command|suggest)\\](?=(?<implicitValue>.*?)\\[/\\k<implicitTag>\\]))");
	private final Logger logger;

	public BBCodeChatParser(Logger logger) {
		this.logger = logger;
	}

	public BBCodeChatParser() {
		this(Logger.getLogger(BBCodeChatParser.class.getName()));
	}

	public BaseComponent[] parse(String text) {
	    Matcher matcher = pattern.matcher(text);
	    TextComponent current = new TextComponent();
	    List<BaseComponent> components = new LinkedList<>();
	    int forceBold = 0;
	    int forceItalic = 0;
	    int forceUnderlined = 0;
	    int forceStrikethrough = 0;
	    int nocolorLevel = 0;
	    int nobbcodeLevel = 0;
	    Deque<ChatColor> colorDeque = new LinkedList<>();
	    Deque<ClickEvent> clickEventDeque = new LinkedList<>();
	    Deque<HoverEvent> hoverEventDeque = new LinkedList<>();
	    while (matcher.find()) {
	      boolean parsed = false;

	      StringBuffer stringBuffer = new StringBuffer();
	      matcher.appendReplacement(stringBuffer, "");
	      TextComponent component = new TextComponent(current);
	      current.setText(stringBuffer.toString());
	      components.add(current);
	      current = component;

	      String group_color = matcher.group("color");
	      String group_tag = matcher.group("tag");
	      String group_value = matcher.group("value");
	      String group_implicitTag = matcher.group("implicitTag");
	      String group_implicitValue = matcher.group("implicitValue");
	      if ((group_color != null) && (nocolorLevel <= 0)){
	        ChatColor color = ChatColor.getByChar(group_color.charAt(0));
	        if (color != null){
	          switch (color){
	          case MAGIC: 
	            current.setObfuscated(true);
	            break;
	          case BOLD: 
	            current.setBold(true);
	            break;
	          case STRIKETHROUGH: 
	            current.setStrikethrough(true);
	            break;
	          case UNDERLINE: 
	            current.setUnderlined(true);
	            break;
	          case ITALIC: 
	            current.setItalic(true);
	            break;
	          case RESET: 
	            color = ChatColor.WHITE;
	          default: 
	            current = new TextComponent();
	            current.setColor(color);
	            current.setBold(Boolean.valueOf(forceBold > 0));
	            current.setItalic(Boolean.valueOf(forceItalic > 0));
	            current.setUnderlined(Boolean.valueOf(forceUnderlined > 0));
	            current.setStrikethrough(Boolean.valueOf(forceStrikethrough > 0));
	            if (!colorDeque.isEmpty()) {
	              current.setColor((ChatColor)colorDeque.peek());
	            }
	            if (!clickEventDeque.isEmpty()) {
	              current.setClickEvent((ClickEvent)clickEventDeque.peek());
	            }
	            if (!hoverEventDeque.isEmpty()) {
	              current.setHoverEvent((HoverEvent)hoverEventDeque.peek());
	            }
	            break;
	          }
	          parsed = true;
	        }
	      }
	      if ((group_tag != null) && (nobbcodeLevel <= 0)){
	        if (group_tag.matches("(?is)^b$")){
	          forceBold++;
	          if (forceBold > 0) {
	            current.setBold(Boolean.valueOf(true));
	          } else {
	            current.setBold(Boolean.valueOf(false));
	          }
	          parsed = true;
	        }
	        else if (group_tag.matches("(?is)^/b$")){
	          forceBold--;
	          if (forceBold <= 0) {
	            current.setBold(Boolean.valueOf(false));
	          } else {
	            current.setBold(Boolean.valueOf(true));
	          }
	          parsed = true;
	        }
	        if (group_tag.matches("(?is)^i$")){
	          forceItalic++;
	          if (forceItalic > 0) {
	            current.setItalic(Boolean.valueOf(true));
	          } else {
	            current.setItalic(Boolean.valueOf(false));
	          }
	          parsed = true;
	        }
	        else if (group_tag.matches("(?is)^/i$")){
	          forceItalic--;
	          if (forceItalic <= 0) {
	            current.setItalic(Boolean.valueOf(false));
	          } else {
	            current.setItalic(Boolean.valueOf(true));
	          }
	          parsed = true;
	        }
	        if (group_tag.matches("(?is)^u$")){
	          forceUnderlined++;
	          if (forceUnderlined > 0) {
	            current.setUnderlined(Boolean.valueOf(true));
	          } else {
	            current.setUnderlined(Boolean.valueOf(false));
	          }
	          parsed = true;
	        }
	        else if (group_tag.matches("(?is)^/u$")){
	          forceUnderlined--;
	          if (forceUnderlined <= 0) {
	            current.setUnderlined(Boolean.valueOf(false));
	          } else {
	            current.setUnderlined(Boolean.valueOf(true));
	          }
	          parsed = true;
	        }
	        if (group_tag.matches("(?is)^s$")){
	          forceStrikethrough++;
	          if (forceStrikethrough > 0) {
	            current.setStrikethrough(Boolean.valueOf(true));
	          } else {
	            current.setStrikethrough(Boolean.valueOf(false));
	          }
	          parsed = true;
	        }
	        else if (group_tag.matches("(?is)^/s$")){
	          forceStrikethrough--;
	          if (forceStrikethrough <= 0) {
	            current.setStrikethrough(Boolean.valueOf(false));
	          } else {
	            current.setStrikethrough(Boolean.valueOf(true));
	          }
	          parsed = true;
	        }
	        if (group_tag.matches("(?is)^color=.*$")){
	          ChatColor color = null;
	          for(ChatColor color1 : ChatColor.values()) {
	            if (color1.getName().equalsIgnoreCase(group_value)) {
	              color = color1;
	            }
	          }
	          colorDeque.push(current.getColor());
	          if ((color != null) && (color != ChatColor.BOLD) && (color != ChatColor.ITALIC) && (color != ChatColor.MAGIC) && (color != ChatColor.RESET) && (color != ChatColor.STRIKETHROUGH) && (color != ChatColor.UNDERLINE)){
	            colorDeque.push(color);
	            current.setColor(color);
	          }
	          else{
	            this.logger.warning("Invalid color tag: [" + group_tag + "] UNKNOWN COLOR '" + group_value + "'");
	            colorDeque.push(ChatColor.WHITE);
	            current.setColor(ChatColor.WHITE);
	          }
	          parsed = true;
	        }
	        else if (group_tag.matches("(?is)^/color$")){
	          if (!colorDeque.isEmpty())
	          {
	            colorDeque.pop();
	            current.setColor((ChatColor)colorDeque.pop());
	          }
	          parsed = true;
	        }
	        if (group_tag.matches("(?is)^url=.*$")){
	          String url = group_value;
	          url = url.replaceAll("(?is)\\[/?nobbcode\\]", "");
	          if (!url.startsWith("http")) {
	            url = "http://" + url;
	          }
	          ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.OPEN_URL, url);
	          clickEventDeque.push(clickEvent);
	          current.setClickEvent(clickEvent);
	          parsed = true;
	        }
	        if (group_tag.matches("(?is)^/(?:url|command|suggest)$")){
	          if (!clickEventDeque.isEmpty()) {
	            clickEventDeque.pop();
	          }
	          if (clickEventDeque.isEmpty()) {
	            current.setClickEvent(null);
	          } else {
	            current.setClickEvent((ClickEvent)clickEventDeque.peek());
	          }
	          parsed = true;
	        }
	        if (group_tag.matches("(?is)^command=.*")){
	          group_value = group_value.replaceAll("(?is)\\[/?nobbcode\\]", "");
	          ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, group_value);
	          clickEventDeque.push(clickEvent);
	          current.setClickEvent(clickEvent);
	          parsed = true;
	        }
	        if (group_tag.matches("(?is)^suggest=.*")){
	          group_value = group_value.replaceAll("(?is)\\[/?nobbcode\\]", "");
	          ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, group_value);
	          clickEventDeque.push(clickEvent);
	          current.setClickEvent(clickEvent);
	          parsed = true;
	        }
	        if (group_tag.matches("(?is)^hover=.*$")){
	          BaseComponent[] components1 = parse(group_value);
	          if (!hoverEventDeque.isEmpty())
	          {
	            BaseComponent[] components2 = ((HoverEvent)hoverEventDeque.getLast()).getValue();
	            BaseComponent[] components3 = new BaseComponent[components1.length + components2.length + 1];
	            int i = 0;
	            for (BaseComponent baseComponent : components2) {
	              components3[(i++)] = baseComponent;
	            }
	            components3[(i++)] = new TextComponent("\n");
	            for (BaseComponent baseComponent : components1) {
	              components3[(i++)] = baseComponent;
	            }
	            components1 = components3;
	          }
	          HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, components1);
	          hoverEventDeque.push(hoverEvent);
	          current.setHoverEvent(hoverEvent);
	          parsed = true;
	        }
	        else if (group_tag.matches("(?is)^/hover$")){
	          if (!hoverEventDeque.isEmpty()) {
	            hoverEventDeque.pop();
	          }
	          if (hoverEventDeque.isEmpty()) {
	            current.setHoverEvent(null);
	          } else {
	            current.setHoverEvent((HoverEvent)hoverEventDeque.peek());
	          }
	          parsed = true;
	        }
	      }
	      if ((group_implicitTag != null) && (nobbcodeLevel <= 0)){
	        if (group_implicitTag.matches("(?is)^url$")){
	          String url = group_implicitValue;
	          if (!url.startsWith("http")) {
	            url = "http://" + url;
	          }
	          ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.OPEN_URL, url);
	          clickEventDeque.push(clickEvent);
	          current.setClickEvent(clickEvent);
	          parsed = true;
	        }
	        if (group_implicitTag.matches("(?is)^command$")){
	          ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, group_implicitValue);
	          clickEventDeque.push(clickEvent);
	          current.setClickEvent(clickEvent);
	          parsed = true;
	        }
	        if (group_implicitTag.matches("(?is)^suggest$")){
	          ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, group_implicitValue);
	          clickEventDeque.push(clickEvent);
	          current.setClickEvent(clickEvent);
	          parsed = true;
	        }
	      }
	      if (group_tag != null){
	        if (group_tag.matches("(?is)^nocolor$")){
	          nocolorLevel++;
	          parsed = true;
	        }
	        if (group_tag.matches("(?is)^/nocolor$")){
	          nocolorLevel--;
	          parsed = true;
	        }
	        if (group_tag.matches("(?is)^nobbcode$")){
	          nobbcodeLevel++;
	          parsed = true;
	        }
	        if (group_tag.matches("(?is)^/nobbcode$")){
	          nobbcodeLevel--;
	          parsed = true;
	        }
	      }
	      if (!parsed){
	        TextComponent component1 = new TextComponent(current);
	        current.setText(matcher.group(0));
	        components.add(current);
	        current = component1;
	      }
	    }
	    StringBuffer stringBuffer = new StringBuffer();
	    matcher.appendTail(stringBuffer);
	    current.setText(stringBuffer.toString());
	    components.add(current);
	    return (BaseComponent[])components.toArray(new BaseComponent[components.size()]);
	}

	public static String stripBBCode(String string) {
		return strip_bbcode_pattern.matcher(string).replaceAll("");
	}
}