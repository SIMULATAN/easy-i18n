package de.marhali.easyi18n.service;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

import de.marhali.easyi18n.InstanceManager;
import de.marhali.easyi18n.action.*;
import de.marhali.easyi18n.tabs.TableView;
import de.marhali.easyi18n.tabs.TreeView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Tool window factory which will represent the entire ui for this plugin.
 * @author marhali
 */
public class TranslatorToolWindowFactory implements ToolWindowFactory, DumbAware {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        InstanceManager manager = InstanceManager.get(project);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();

        // Translations tree view
        TreeView treeView = new TreeView(project);
        Content treeContent = contentFactory.createContent(treeView.getRootPanel(),
                ResourceBundle.getBundle("messages").getString("view.tree.title"), false);

        toolWindow.getContentManager().addContent(treeContent);

        // Translations table view
        TableView tableView = new TableView(project);
        Content tableContent = contentFactory.createContent(tableView.getRootPanel(),
                ResourceBundle.getBundle("messages").getString("view.table.title"), false);

        toolWindow.getContentManager().addContent(tableContent);

        // ToolWindow Actions (Can be used for every view)
        List<AnAction> actions = new ArrayList<>();
        actions.add(new AddAction());
        actions.add(new FilterIncompleteAction());
        actions.add(new FilterDuplicateAction());
        actions.add(new ReloadAction());
        actions.add(new SettingsAction());
        actions.add(new SearchAction((query) -> manager.bus().propagate().onSearchQuery(query)));
        toolWindow.setTitleActions(actions);

        // Initialize Window Manager
        WindowManager.getInstance().initialize(toolWindow, treeView, tableView);

        // Synchronize ui with underlying data
        manager.uiBus().addListener(treeView);
        manager.uiBus().addListener(tableView);
        manager.bus().propagate().onUpdateData(manager.store().getData());
    }
}