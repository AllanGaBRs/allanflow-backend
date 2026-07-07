package com.allan.task.manager;

import com.allan.task.manager.board.BoardModel;
import com.allan.task.manager.board.BoardRepository;
import com.allan.task.manager.client.ClientModel;
import com.allan.task.manager.client.ClientRepository;
import com.allan.task.manager.column.ColumnModel;
import com.allan.task.manager.column.ColumnRepository;
import com.allan.task.manager.label.LabelModel;
import com.allan.task.manager.label.LabelRepository;
import com.allan.task.manager.membership.MembershipModel;
import com.allan.task.manager.membership.MembershipRepository;
import com.allan.task.manager.task.TaskModel;
import com.allan.task.manager.task.TaskRepository;
import com.allan.task.manager.user.UserModel;
import com.allan.task.manager.user.UserRepository;
import com.allan.task.manager.workspace.WorkspaceModel;
import com.allan.task.manager.workspace.WorkspaceRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.HashSet;
import java.util.Set;

@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


    @Bean
    CommandLineRunner run(
            UserRepository userRepository,
            WorkspaceRepository workspaceRepository,
            MembershipRepository membershipRepository,
            BoardRepository boardRepository,
            ColumnRepository columnRepository,
            LabelRepository labelRepository,
            TaskRepository taskRepository,
            ClientRepository clientRepository,
            PasswordEncoder passwordEncoder
    ) {

        return args -> {

            if (userRepository.count() > 0) {
                return;
            }


            /*
             * USERS
             */

            UserModel allan = new UserModel();
            allan.setName("Allan");
            allan.setEmail("allan@email.com");
            allan.setPassword(passwordEncoder.encode("123456"));
            allan.setRole(UserModel.Role.ROLE_USER);


            UserModel joao = new UserModel();
            joao.setName("João");
            joao.setEmail("joao@email.com");
            joao.setPassword(passwordEncoder.encode("123456"));
            joao.setRole(UserModel.Role.ROLE_USER);


            UserModel maria = new UserModel();
            maria.setName("Maria");
            maria.setEmail("maria@email.com");
            maria.setPassword(passwordEncoder.encode("123456"));
            maria.setRole(UserModel.Role.ROLE_USER);


            userRepository.saveAll(
                    Set.of(allan, joao, maria)
            );


            /*
             * WORKSPACE
             */

            WorkspaceModel workspace = new WorkspaceModel();

            workspace.setName("Allan Software");
            workspace.setSlug("allan-software");
            workspace.setOwner(allan);

            workspaceRepository.save(workspace);


            /*
             * MEMBERSHIP
             */

            MembershipModel owner = MembershipModel.builder()
                    .user(allan)
                    .workspace(workspace)
                    .role(MembershipModel.MembershipRole.OWNER)
                    .build();


            MembershipModel admin = MembershipModel.builder()
                    .user(joao)
                    .workspace(workspace)
                    .role(MembershipModel.MembershipRole.ADMIN)
                    .build();


            MembershipModel member = MembershipModel.builder()
                    .user(maria)
                    .workspace(workspace)
                    .role(MembershipModel.MembershipRole.MEMBER)
                    .build();


            membershipRepository.saveAll(
                    Set.of(owner, admin, member)
            );


            /*
             * BOARD
             */

            BoardModel board = BoardModel.builder()
                    .name("Backend")
                    .description("Backend development board")
                    .workspace(workspace)
                    .members(new HashSet<>(
                            Set.of(allan, joao, maria)
                    ))
                    .build();

            boardRepository.save(board);


            /*
             * COLUMNS
             */

            ColumnModel todo = ColumnModel.builder()
                    .name("Todo")
                    .position(0)
                    .board(board)
                    .workspace(workspace)
                    .build();


            ColumnModel doing = ColumnModel.builder()
                    .name("Doing")
                    .position(1)
                    .board(board)
                    .workspace(workspace)
                    .build();


            ColumnModel review = ColumnModel.builder()
                    .name("Review")
                    .position(2)
                    .board(board)
                    .workspace(workspace)
                    .build();


            ColumnModel done = ColumnModel.builder()
                    .name("Done")
                    .position(3)
                    .board(board)
                    .workspace(workspace)
                    .build();


            columnRepository.saveAll(
                    Set.of(todo, doing, review, done)
            );


            /*
             * LABELS
             */

            LabelModel bug = new LabelModel();
            bug.setName("Bug");
            bug.setColor("#EF4444");
            bug.setBoard(board);


            LabelModel feature = new LabelModel();
            feature.setName("Feature");
            feature.setColor("#22C55E");
            feature.setBoard(board);


            labelRepository.saveAll(
                    Set.of(bug, feature)
            );


            /*
             * CLIENT
             */

            ClientModel client = new ClientModel();

            client.setName("OpenAI");
            client.setEmail("contact@openai.com");
            client.setCompany("OpenAI");
            client.setWorkspace(workspace);

            clientRepository.save(client);


            /*
             * TASKS
             */

            TaskModel task = TaskModel.builder()
                    .title("Implement JWT authentication")
                    .description("Create authentication flow")
                    .workspace(workspace)
                    .board(board)
                    .column(todo)
                    .position(0)
                    .priority(TaskModel.Priority.HIGH)
                    .labels(new HashSet<>(Set.of(feature)))
                    .assignees(new HashSet<>(Set.of(allan)))
                    .client(client)
                    .build();


            TaskModel task2 = TaskModel.builder()
                    .title("Fix login bug")
                    .description("Fix authentication issue")
                    .workspace(workspace)
                    .board(board)
                    .column(todo)
                    .position(1)
                    .priority(TaskModel.Priority.HIGH)
                    .labels(new HashSet<>(Set.of(bug)))
                    .assignees(new HashSet<>(Set.of(joao)))
                    .build();


            taskRepository.saveAll(
                    Set.of(task, task2)
            );

        };
    }
}