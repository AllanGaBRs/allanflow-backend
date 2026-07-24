package com.allan.task.manager.factory;

import com.allan.task.manager.board.BoardModel;
import com.allan.task.manager.checklist.ChecklistModel;
import com.allan.task.manager.checklistItem.ChecklistItemModel;
import com.allan.task.manager.column.ColumnModel;
import com.allan.task.manager.comment.CommentModel;
import com.allan.task.manager.membership.MembershipModel;
import com.allan.task.manager.task.TaskModel;
import com.allan.task.manager.user.UserModel;
import com.allan.task.manager.user.dto.UserRegisterDTO;
import com.allan.task.manager.workspace.WorkspaceModel;

import java.util.UUID;

public class Factory {

    public static UserRegisterDTO createUserRegisterDTO() {
        return new UserRegisterDTO(
                "Test User",
                "test-user-" + UUID.randomUUID() + "@example.com",
                "123456"
        );
    }

    public static UserModel createUserModel() {
        UserModel user = new UserModel();
        user.setId(UUID.randomUUID());
        user.setName("Test User");
        user.setEmail("test-user-" + UUID.randomUUID() + "@example.com");
        user.setPassword("encoded-password");
        user.setRole(UserModel.Role.ROLE_USER);
        user.setActive(true);
        return user;
    }

    public static WorkspaceModel createWorkspaceModel() {
        UserModel owner = createUserModel();

        WorkspaceModel workspace = new WorkspaceModel();
        workspace.setId(UUID.randomUUID());
        workspace.setName("Test Workspace");
        workspace.setSlug("test-workspace-" + UUID.randomUUID());
        workspace.setOwner(owner);
        workspace.setActive(true);
        return workspace;
    }

    public static MembershipModel createMembershipModel(
            MembershipModel.MembershipRole role
    ) {
        MembershipModel membership = new MembershipModel();
        membership.setId(UUID.randomUUID());
        membership.setUser(createUserModel());
        membership.setWorkspace(createWorkspaceModel());
        membership.setRole(role);
        return membership;
    }

    public static BoardModel createBoardModel() {
        BoardModel board = new BoardModel();
        board.setId(UUID.randomUUID());
        board.setName("Test Board");
        board.setWorkspace(createWorkspaceModel());
        return board;
    }

    public static ColumnModel createColumnModel(BoardModel board, int position) {
        ColumnModel column = new ColumnModel();
        column.setId(UUID.randomUUID());
        column.setName("Test Column");
        column.setPosition(position);
        column.setBoard(board);
        column.setWorkspace(board.getWorkspace());
        return column;
    }

    public static TaskModel createTaskModel(ColumnModel column, int position) {
        TaskModel task = new TaskModel();
        task.setId(UUID.randomUUID());
        task.setTitle("Test Task");
        task.setWorkspace(column.getWorkspace());
        task.setBoard(column.getBoard());
        task.setColumn(column);
        task.setPosition(position);
        task.setPriority(TaskModel.Priority.MEDIUM);
        task.setArchived(false);
        return task;
    }

    public static CommentModel createCommentModel(
            TaskModel task,
            UserModel author
    ) {
        CommentModel comment = new CommentModel();
        comment.setId(UUID.randomUUID());
        comment.setContent("Test Comment");
        comment.setTask(task);
        comment.setAuthor(author);
        return comment;
    }

    public static ChecklistModel createChecklistModel(TaskModel task) {
        ChecklistModel checklist = new ChecklistModel();
        checklist.setId(UUID.randomUUID());
        checklist.setTitle("Test Checklist");
        checklist.setTask(task);
        return checklist;
    }

    public static ChecklistItemModel createChecklistItemModel(
            ChecklistModel checklist,
            int position
    ) {
        ChecklistItemModel item = new ChecklistItemModel();
        item.setId(UUID.randomUUID());
        item.setContent("Test Checklist Item");
        item.setChecked(false);
        item.setPosition(position);
        item.setChecklist(checklist);
        return item;
    }
}
