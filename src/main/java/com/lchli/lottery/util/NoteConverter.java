package com.lchli.lottery.util;

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.thymeleaf.util.StringUtils;

public final class NoteConverter {

    public static String convertNoteContentToHtml(String jsonContent) {
        if (StringUtils.isEmpty(jsonContent)) {
            return null;
        }

        try {
            StringBuilder sb = new StringBuilder();
            JSONArray jsonArray = new JSONArray(jsonContent);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                String htmlTag = convertElementToHtmlTag(jsonObject);
                sb.append(htmlTag);
            }

            return sb.toString();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;

    }

    private static String convertElementToHtmlTag(JSONObject jsonObject) {
        if (jsonObject == null) {
            return "";
        }
        String type = jsonObject.optString("type");
        if (type == null) {
            return "";
        }
        switch (type) {
            case "text":
                String text = jsonObject.optString("text");
                if (text != null) {
                    text = text.replaceAll("\n", "<br>");
                }
                return String.format("<p>%s</p>", text);
            case "img":
                return String.format("<br><img src=\"%s\"/>", jsonObject.optString("path"));

            case "audio":
                return String.format("<br><br><audio controls><source src=\"%s\" type=\"audio/mpeg\"> </audio><br>", jsonObject.optString("path"));

            case "video":
                return String.format("<br><video controls><source src=\"%s\" type=\"video/mp4\"> </video>", jsonObject.optString("path"));

            default:
                return "";
        }
    }
}








