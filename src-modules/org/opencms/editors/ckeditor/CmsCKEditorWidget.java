/*
 * File   : $Source: /alkacon/cvs/opencms/src-modules/org/opencms/editors/ckeditor/CmsCKEditorWidget.java,v $
 * Date   : $Date: 2009/10/28 10:37:59 $
 * Version: $Revision: 1.1 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (c) 2002 - 2009 Alkacon Software GmbH (http://www.alkacon.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * For further information about Alkacon Software GmbH, please see the
 * company website: http://www.alkacon.com
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.opencms.editors.ckeditor;

import org.opencms.file.CmsObject;
import org.opencms.i18n.CmsEncoder;
import org.opencms.main.OpenCms;
import org.opencms.util.CmsStringUtil;
import org.opencms.widgets.A_CmsHtmlWidget;
import org.opencms.widgets.CmsHtmlWidgetOption;
import org.opencms.widgets.I_CmsWidget;
import org.opencms.widgets.I_CmsWidgetDialog;
import org.opencms.widgets.I_CmsWidgetParameter;
import org.opencms.workplace.CmsWorkplace;
import org.opencms.workplace.editors.CmsEditor;
import org.opencms.workplace.editors.I_CmsEditorCssHandler;
import org.opencms.xml.types.I_CmsXmlContentValue;

import java.util.Iterator;
import java.util.Map;

/**
 * Provides a widget that creates a rich input field using the "CKEditor" component, for use on a widget dialog.<p>
 * 
 * For configuration options, have a look at the documentation of {@link CmsHtmlWidgetOption}.<p>
 *
 * @author Andreas Zahner
 * 
 * @version $Revision: 1.1 $ 
 * 
 * @since 8.0
 */
public class CmsCKEditorWidget extends A_CmsHtmlWidget {

    /** The translation of the generic widget button names to CKEditor specific button names. */
    public static final String BUTTON_TRANSLATION = CmsHtmlWidgetOption.BUTTONBAR_SEPARATOR
        + ":'-'|undo:'Undo'|redo:'Redo'|find:'Find'|replace:'Replace'|selectall:'SelectAll'|removeformat:'RemoveFormat'"
        + "|cut:'Cut'|copy:'Copy'|paste:'Paste','PasteText','PasteFromWord'|bold:'Bold'|italic:'Italic'|underline:'Underline'|strikethrough:'Strike'"
        + "|subscript:'Subscript'|superscript:'Superscript'|alignleft:'JustifyLeft'|aligncenter:'JustifyCenter'|alignright:'JustifyRight'|justify:'JustifyBlock'"
        + "|orderedlist:'NumberedList'|unorderedlist:'BulletedList'|outdent:'Outdent'|indent:'Indent'|source:'Source'|formatselect:'Format'|link:'oc-link'|editorlink:'Link'"
        + "|anchor:'Anchor'|unlink:'Unlink'|imagegallery:'OcmsImageGallery'|downloadgallery:'OcmsDownloadGallery'|linkgallery:'OcmsLinkGallery'|htmlgallery:'OcmsHtmlGallery'|tablegallery:'OcmsTableGallery'"
        + "|table:'Table'|specialchar:'SpecialChar'|print:'Print'|spellcheck:'SpellChecker'|fitwindow:'Maximize'|style:'Styles'";

    /** The map containing the translation of the generic widget button names to CKEditor specific button names. */
    public static final Map<String, String> BUTTON_TRANSLATION_MAP = CmsStringUtil.splitAsMap(
        BUTTON_TRANSLATION,
        "|",
        ":");

    /** Request parameter name for the tool bar configuration parameter. */
    public static final String PARAM_CONFIGURATION = "config";

    /**
     * Creates a new CKEditor widget.<p>
     */
    public CmsCKEditorWidget() {

        // empty constructor is required for class registration
        this("");
    }

    /**
     * Creates a new CKEditor widget with the given configuration.<p>
     * 
     * @param configuration the configuration to use
     */
    public CmsCKEditorWidget(CmsHtmlWidgetOption configuration) {

        super(configuration);
    }

    /**
     * Creates a new CKEditor widget with the given configuration.<p>
     * 
     * @param configuration the configuration to use
     */
    public CmsCKEditorWidget(String configuration) {

        super(configuration);
    }

    /**
     * Returns the button bar configuration String for the CKEditor widget.<p>
     * 
     * @param configuration the configuration to use
     * @return the button bar configuration String for the CKeditor widget
     */
    public static String getButtonBar(CmsHtmlWidgetOption configuration) {

        return configuration.getButtonBar(BUTTON_TRANSLATION_MAP, ",");
    }

    /**
     * Returns the individual options for the format select box, if they were configured, otherwise an empty String.<p>
     * 
     * @param option the configured HTML widget options
     * 
     * @return the individual options for the format select box
     */
    public static String getFormatSelectOptionsConfiguration(CmsHtmlWidgetOption option) {

        if (CmsStringUtil.isNotEmpty(option.getFormatSelectOptions())) {
            // individual options are used, create configuration output
            return "config.format_tags = \"" + option.getFormatSelectOptions() + "\",";
        }
        return "";
    }

    /**
     * @see org.opencms.widgets.I_CmsWidget#getDialogIncludes(org.opencms.file.CmsObject, org.opencms.widgets.I_CmsWidgetDialog)
     */
    @Override
    public String getDialogIncludes(CmsObject cms, I_CmsWidgetDialog widgetDialog) {

        StringBuffer result = new StringBuffer(128);
        // general CKEditor JS
        result.append(getJSIncludeFile(CmsWorkplace.getSkinUri() + "editors/ckeditor/ckeditor.js"));
        result.append("\n");
        // special CKEditor widget functions
        result.append(getJSIncludeFile(CmsWorkplace.getSkinUri() + "components/widgets/ckeditor.js"));
        return result.toString();
    }

    /**
     * @see org.opencms.widgets.I_CmsWidget#getDialogInitCall(org.opencms.file.CmsObject, org.opencms.widgets.I_CmsWidgetDialog)
     */
    @Override
    public String getDialogInitCall(CmsObject cms, I_CmsWidgetDialog widgetDialog) {

        // creates the CKEditor instances
        return "\tinitCKEditor();\n";
    }

    /**
     * @see org.opencms.widgets.I_CmsWidget#getDialogInitMethod(org.opencms.file.CmsObject, org.opencms.widgets.I_CmsWidgetDialog)
     */
    @Override
    public String getDialogInitMethod(CmsObject cms, I_CmsWidgetDialog widgetDialog) {

        StringBuffer result = new StringBuffer(512);
        result.append("function initCKEditor() {\n");

        // register plugins for OpenCms
        String galleryPluginPath = OpenCms.getLinkManager().substituteLink(
            cms,
            "/system/workplace/editors/ckeditor/plugins/galleries/");
        result.append("\tCKEDITOR.plugins.addExternal(\"opencms\", \"").append(
            OpenCms.getLinkManager().substituteLink(cms, "/system/workplace/editors/ckeditor/plugins/opencms/")).append(
            "\");\n");
        result.append("\tCKEDITOR.plugins.addExternal(\"imagegallery\", \"").append(galleryPluginPath).append(
            "\", \"imagegallery.js\");\n");
        result.append("\tCKEDITOR.plugins.addExternal(\"downloadgallery\", \"").append(galleryPluginPath).append(
            "\", \"downloadgallery.js\");\n");
        result.append("\tCKEDITOR.plugins.addExternal(\"linkgallery\", \"").append(galleryPluginPath).append(
            "\", \"linkgallery.js\");\n");
        result.append("\tCKEDITOR.plugins.addExternal(\"htmlgallery\", \"").append(galleryPluginPath).append(
            "\", \"htmlgallery.js\");\n");
        result.append("\tCKEDITOR.plugins.addExternal(\"tablegallery\", \"").append(galleryPluginPath).append(
            "\", \"tablegallery.js\");\n");

        // set time out for IE to avoid tool bar error message on direct publish button click
        result.append("\tif (navigator.userAgent.toLowerCase().indexOf(\"msie\") != -1) {\n");
        result.append("\t\tsetTimeout(\"generateEditors();\", 50);\n");
        result.append("\t} else {");
        result.append("\t\tgenerateEditors();\n");
        result.append("\t}\n");
        result.append("}\n");
        return result.toString();
    }

    /**
     * @see org.opencms.widgets.I_CmsWidget#getDialogWidget(org.opencms.file.CmsObject, org.opencms.widgets.I_CmsWidgetDialog, org.opencms.widgets.I_CmsWidgetParameter)
     */
    public String getDialogWidget(CmsObject cms, I_CmsWidgetDialog widgetDialog, I_CmsWidgetParameter param) {

        String id = param.getId();
        String value = param.getStringValue(cms);
        StringBuffer result = new StringBuffer(4096);

        result.append("<td class=\"xmlTd\">");

        result.append("<textarea class=\"xmlInput maxwidth\" name=\"ta_");
        result.append(id);
        result.append("\" id=\"ta_");
        result.append(id);
        result.append("\" style=\"height: ");
        result.append(getHtmlWidgetOption().getEditorHeight());
        result.append(";\" rows=\"20\" cols=\"60\">");
        result.append(CmsEncoder.escapeXml(value));
        result.append("</textarea>");
        result.append("<input type=\"hidden\" name=\"");
        result.append(id);
        result.append("\" id=\"");
        result.append(id);
        result.append("\" value=\"");
        result.append(CmsEncoder.encode(value));
        result.append("\">");

        // generate the special configuration object for the current editor widget
        result.append("<script type=\"text/javascript\">\n");
        result.append("tmpEditorSettings = new Object();\n");

        // set CSS style sheet for current editor widget if configured
        boolean cssConfigured = false;
        String cssPath = "";
        if (getHtmlWidgetOption().useCss()) {
            cssPath = getHtmlWidgetOption().getCssPath();
            // set the CSS path to null (the created configuration String passed to JS will not include this path then)
            getHtmlWidgetOption().setCssPath(null);
            cssConfigured = true;
        } else if (OpenCms.getWorkplaceManager().getEditorCssHandlers().size() > 0) {
            Iterator<I_CmsEditorCssHandler> i = OpenCms.getWorkplaceManager().getEditorCssHandlers().iterator();
            try {
                // cast parameter to I_CmsXmlContentValue
                I_CmsXmlContentValue contentValue = (I_CmsXmlContentValue)param;
                // now extract the absolute path of the edited resource
                String editedResource = cms.getSitePath(contentValue.getDocument().getFile());
                while (i.hasNext()) {
                    I_CmsEditorCssHandler handler = i.next();
                    if (handler.matches(cms, editedResource)) {
                        cssPath = handler.getUriStyleSheet(cms, editedResource);
                        if (CmsStringUtil.isNotEmptyOrWhitespaceOnly(cssPath)) {
                            cssConfigured = true;
                        }
                        break;
                    }
                }
            } catch (Exception e) {
                // ignore, CSS could not be set
            }
        }

        result.append("tmpEditorSettings.contentsCss = \"");
        if (cssConfigured) {
            result.append(OpenCms.getLinkManager().substituteLink(cms, cssPath));
        }
        result.append("\";\n");

        // set styles XML for current editor widget if configured
        result.append("tmpEditorSettings.stylesCombo_stylesSet = \"");
        if (getHtmlWidgetOption().showStylesXml()) {
            result.append(OpenCms.getLinkManager().substituteLink(cms, getHtmlWidgetOption().getStylesXmlPath()));
            // set the styles XML path to a value that the JS will create the selector
            getHtmlWidgetOption().setStylesXmlPath("true");
        }
        result.append("\";\n");

        // set full page mode for current editor widget if configured
        result.append("tmpEditorSettings.fullPage = ");
        result.append(getHtmlWidgetOption().isFullPage() ? "true" : "false");
        result.append(";\n");

        // store id of input field and textarea
        result.append("tmpEditorSettings.areaId = \"");
        result.append(id);
        result.append("\";\n");

        // generate the special configuration JS call for the current dialog widget
        StringBuffer configJs = new StringBuffer(128);
        configJs.append(CmsEditor.PATH_EDITORS);
        configJs.append("ckeditor/configwidget.js");
        configJs.append("?");
        configJs.append(PARAM_CONFIGURATION);
        configJs.append("=");
        configJs.append(CmsHtmlWidgetOption.createConfigurationString(getHtmlWidgetOption()));
        result.append("tmpEditorSettings.configPath = \"");
        result.append(OpenCms.getLinkManager().substituteLink(cms, configJs.toString()));
        result.append("\";\n");
        result.append("editorInstances[editorInstances.length] = tmpEditorSettings;\n");
        result.append("contentFields[contentFields.length] = document.getElementById(\"").append(id).append("\");\n");
        result.append("</script>\n");

        result.append("</td>");

        return result.toString();
    }

    /**
     * @see org.opencms.widgets.I_CmsWidget#newInstance()
     */
    public I_CmsWidget newInstance() {

        return new CmsCKEditorWidget(getHtmlWidgetOption());
    }

}