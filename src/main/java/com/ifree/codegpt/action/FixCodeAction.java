package com.ifree.codegpt.action;

import com.ifree.codegpt.generator.CodeExplainer;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

/**
 * @author tian
 */
public class FixCodeAction extends BaseTextPreHandler {
    @Override
    public void update(AnActionEvent e) {
        // Enable the menu item only if a file is selected
        VirtualFile file = e.getData(CommonDataKeys.VIRTUAL_FILE);
        e.getPresentation().setEnabledAndVisible(file != null && !file.isDirectory());
    }

    @Override
    CodeExplainer.ExplainerType getExplainerType() {
        return CodeExplainer.ExplainerType.CODE_FIX;
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return super.getActionUpdateThread();
    }
}

