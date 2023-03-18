package com.ifree.codegpt.generator;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.components.JBScrollPane;
import com.ifree.codegpt.repository.api.OpenAIChatApi;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.CompletableFuture;

/**
 * @author tian
 */
public class CodeExplainer {


    @SuppressWarnings("AlibabaEnumConstantsMustHaveComment")
    public enum ExplainerType {
        CODE_EXPLAIN,
        CODE_COMMENT
    }

    String apiKey;
    String contents;

    String language;
    AnActionEvent e;

    public CodeExplainer(String apiKey, String contents, AnActionEvent e, String language) {
        this.apiKey = apiKey;
        this.contents = contents;
        this.e = e;
        this.language = language;
    }

    public void explain(ExplainerType type) {

        OpenAIChatApi api = new OpenAIChatApi(apiKey);

        String model = "gpt-3.5-turbo";
        OpenAIChatApi.ChatMessageRequest[] messages = new OpenAIChatApi.ChatMessageRequest[0];
        if (type == ExplainerType.CODE_EXPLAIN) {
            messages = new OpenAIChatApi.ChatMessageRequest[]{
                    new OpenAIChatApi.ChatMessageRequest("user",
                            "解释这段" + language + "代码作用\n" + contents)
            };
        } else if (type == ExplainerType.CODE_COMMENT) {
            messages = new OpenAIChatApi.ChatMessageRequest[]{
                    new OpenAIChatApi.ChatMessageRequest("user",
                            "为这段" + language + "代码生成生成相应的注释,并且保留原代码格式返回\n" + contents)
            };
        }
        CompletableFuture<OpenAIChatApi.ChatCompletionResponse> futureResponse = api.sendChatCompletionRequestAsync(model, messages);

        futureResponse.thenAccept(response -> {
            // Handle successful response
            for (OpenAIChatApi.Choice choice : response.getChoices()) {
                OpenAIChatApi.Message message = choice.getMessage();

                String content = message.getContent();

                ApplicationManager.getApplication().invokeLater(() -> {
                    showRightPanel(content);
                });
            }
        }).exceptionally(ex -> {
            System.out.println("error" + ex.getMessage());
            // Handle exception
            return null;
        });
    }

    private void showRightPanel(String explained) {

        Project project = e.getProject();
        assert project != null;
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        // Find or create the ToolWindow
        ToolWindow toolWindow = toolWindowManager.getToolWindow("CodeGPT - Code Explainer");
        if (toolWindow == null) {
            toolWindow = toolWindowManager.registerToolWindow("CodeGPT - Code Explainer", false,
                    ToolWindowAnchor.RIGHT);
        }
        // Create the Content
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        JTextArea textArea = new JTextArea(explained);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(textArea, BorderLayout.CENTER);
        // Add padding
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JBScrollPane(panel);
        // Remove margin
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(toolWindow.getContentManager().getComponent().getSize());
        Content content = contentFactory.createContent(scrollPane, null, false);
        // Add the Content to the ToolWindow
        toolWindow.getContentManager().removeAllContents(true);
        toolWindow.getContentManager().addContent(content);
        // Show the ToolWindow
        toolWindow.show(null);
    }
}
