package com.ifree.codegpt.action;

import com.ifree.codegpt.generator.TestCaseGenerator;
import com.ifree.codegpt.repository.data.FileManager;
import com.ifree.codegpt.repository.data.LocalData;
import com.ifree.codegpt.settings.SettingsPanel;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

/**
 * @author tian
 */
public class GenerateTestCaseAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        // Get a reference to the current file
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
        String contents = (new FileManager()).readFile(file);
        if (contents == null) {
            return;
        }

        //log the contents
        String apiKey = LocalData.get("apiKey");

        if (apiKey == null || apiKey.isEmpty()) {
            SettingsPanel settingsPanel = new SettingsPanel(e, apiKey1 -> {
                if (apiKey1 != null && !apiKey1.isEmpty()) {
                    (new TestCaseGenerator(apiKey1, contents, file, e)).generateTestCase();
                }

            });
            settingsPanel.show();
            return;
        }

        (new TestCaseGenerator(apiKey, contents, file, e)).generateTestCase();

    }

    @Override
    public void update(AnActionEvent e) {
        // Enable the menu item only if a file is selected
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
        e.getPresentation().setEnabledAndVisible(file != null && !file.isDirectory());
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return super.getActionUpdateThread();
    }
}

