package com.ifree.codegpt.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.ifree.codegpt.generator.CodeExplainer;
import com.ifree.codegpt.repository.data.FileManager;
import com.ifree.codegpt.repository.data.LocalData;
import com.ifree.codegpt.settings.SettingsPanel;

/**
 * @author tian
 */
public abstract class BaseTextPreHandler extends AnAction {
    /**
     * Get the explainer type
     *
     * @return the explainer type
     */
    abstract CodeExplainer.ExplainerType getExplainerType();

    @Override
    public void actionPerformed(AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (editor != null) {
            PsiFile file = e.getData(CommonDataKeys.PSI_FILE);
            if (file != null) {
                String suffix = file.getName().substring(file.getName().lastIndexOf(".") + 1);
                String languageTag = "这是一段" + suffix.toUpperCase() + "语言代码";
                String selectedText = editor.getSelectionModel().getSelectedText();
                if (selectedText != null && !selectedText.isEmpty()) {
                    showExplanation(e, selectedText, languageTag);
                    return;
                }
            }
        }
        // Get a reference to the current file
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
        String contents = (new FileManager()).readFile(file);
        if (contents == null || contents.isEmpty() || file == null) {
            return;
        }
        String suffix = file.getName().substring(file.getName().lastIndexOf(".") + 1);
        String languageTag = "这是一段" + suffix.toUpperCase() + "语言代码";
        showExplanation(e, contents, languageTag);
    }

    private void showExplanation(AnActionEvent e, String selectedText, String language) {
        String apiKey = LocalData.get("apiKey");
        if (apiKey == null || apiKey.isEmpty()) {
            SettingsPanel settingsPanel = new SettingsPanel(e, apiKey1 -> {
                if (apiKey1 != null && !apiKey1.isEmpty()) {
                    (new CodeExplainer(apiKey1, selectedText, e, language)).explain(getExplainerType());
                }
            });
            settingsPanel.show();
            return;
        }
        (new CodeExplainer(apiKey, selectedText, e, language)).explain(getExplainerType());
    }

}
