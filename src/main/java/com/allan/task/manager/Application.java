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
import org.springframework.context.annotation.Profile;
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
    @Profile("dev")
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

            UserModel ana = new UserModel();
            ana.setName("Ana");
            ana.setEmail("ana@email.com");
            ana.setPassword(passwordEncoder.encode("123456"));
            ana.setRole(UserModel.Role.ROLE_USER);

            UserModel carlos = new UserModel();
            carlos.setName("Carlos");
            carlos.setEmail("carlos@email.com");
            carlos.setPassword(passwordEncoder.encode("123456"));
            carlos.setRole(UserModel.Role.ROLE_USER);

            UserModel bianca = new UserModel();
            bianca.setName("Bianca");
            bianca.setEmail("bianca@email.com");
            bianca.setPassword(passwordEncoder.encode("123456"));
            bianca.setRole(UserModel.Role.ROLE_USER);

            userRepository.saveAll(
                    Set.of(allan, joao, maria, ana, carlos, bianca)
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

            MembershipModel financeMember = MembershipModel.builder()
                    .user(ana)
                    .workspace(workspace)
                    .role(MembershipModel.MembershipRole.MEMBER)
                    .build();

            MembershipModel salesMember = MembershipModel.builder()
                    .user(carlos)
                    .workspace(workspace)
                    .role(MembershipModel.MembershipRole.MEMBER)
                    .build();

            MembershipModel marketingMember = MembershipModel.builder()
                    .user(bianca)
                    .workspace(workspace)
                    .role(MembershipModel.MembershipRole.MEMBER)
                    .build();

            membershipRepository.saveAll(
                    Set.of(owner, admin, member, financeMember, salesMember, marketingMember)
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

            BoardModel financeBoard = BoardModel.builder()
                    .name("Financeiro")
                    .description("Contratos, cobranças e fechamento financeiro")
                    .workspace(workspace)
                    .members(new HashSet<>(
                            Set.of(allan, ana)
                    ))
                    .build();

            BoardModel marketingBoard = BoardModel.builder()
                    .name("Marketing")
                    .description("Campanhas, conteúdos e lançamentos")
                    .workspace(workspace)
                    .members(new HashSet<>(
                            Set.of(allan, bianca)
                    ))
                    .build();

            BoardModel salesBoard = BoardModel.builder()
                    .name("Comercial")
                    .description("Leads, propostas e clientes em negociação")
                    .workspace(workspace)
                    .members(new HashSet<>(
                            Set.of(allan, carlos, maria)
                    ))
                    .build();

            boardRepository.saveAll(
                    Set.of(financeBoard, marketingBoard, salesBoard)
            );


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

            ColumnModel financeTodo = ColumnModel.builder()
                    .name("A fazer")
                    .position(0)
                    .board(financeBoard)
                    .workspace(workspace)
                    .build();

            ColumnModel financeWaiting = ColumnModel.builder()
                    .name("Aguardando")
                    .position(1)
                    .board(financeBoard)
                    .workspace(workspace)
                    .build();

            ColumnModel financeDone = ColumnModel.builder()
                    .name("Finalizado")
                    .position(2)
                    .board(financeBoard)
                    .workspace(workspace)
                    .build();

            ColumnModel marketingIdeas = ColumnModel.builder()
                    .name("Ideias")
                    .position(0)
                    .board(marketingBoard)
                    .workspace(workspace)
                    .build();

            ColumnModel marketingDoing = ColumnModel.builder()
                    .name("Produção")
                    .position(1)
                    .board(marketingBoard)
                    .workspace(workspace)
                    .build();

            ColumnModel marketingDone = ColumnModel.builder()
                    .name("Publicado")
                    .position(2)
                    .board(marketingBoard)
                    .workspace(workspace)
                    .build();

            ColumnModel salesLeads = ColumnModel.builder()
                    .name("Leads")
                    .position(0)
                    .board(salesBoard)
                    .workspace(workspace)
                    .build();

            ColumnModel salesProposal = ColumnModel.builder()
                    .name("Proposta")
                    .position(1)
                    .board(salesBoard)
                    .workspace(workspace)
                    .build();

            ColumnModel salesClosed = ColumnModel.builder()
                    .name("Fechado")
                    .position(2)
                    .board(salesBoard)
                    .workspace(workspace)
                    .build();

            columnRepository.saveAll(
                    Set.of(
                            todo,
                            doing,
                            review,
                            done,
                            financeTodo,
                            financeWaiting,
                            financeDone,
                            marketingIdeas,
                            marketingDoing,
                            marketingDone,
                            salesLeads,
                            salesProposal,
                            salesClosed
                    )
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

            LabelModel invoice = new LabelModel();
            invoice.setName("Faturamento");
            invoice.setColor("#0EA5E9");
            invoice.setBoard(financeBoard);

            LabelModel contract = new LabelModel();
            contract.setName("Contrato");
            contract.setColor("#6366F1");
            contract.setBoard(financeBoard);

            LabelModel campaign = new LabelModel();
            campaign.setName("Campanha");
            campaign.setColor("#EC4899");
            campaign.setBoard(marketingBoard);

            LabelModel content = new LabelModel();
            content.setName("Conteúdo");
            content.setColor("#14B8A6");
            content.setBoard(marketingBoard);

            LabelModel lead = new LabelModel();
            lead.setName("Lead");
            lead.setColor("#2563EB");
            lead.setBoard(salesBoard);

            LabelModel proposal = new LabelModel();
            proposal.setName("Proposta");
            proposal.setColor("#7C3AED");
            proposal.setBoard(salesBoard);

            labelRepository.saveAll(
                    Set.of(bug, feature, invoice, contract, campaign, content, lead, proposal)
            );


            /*
             * CLIENT
             */

            ClientModel client1 = new ClientModel();
            client1.setName("OpenAI");
            client1.setEmail("contact@openai.com");
            client1.setPhone("44999990001");
            client1.setCompany("OpenAI");
            client1.setWorkspace(workspace);
            clientRepository.save(client1);

            ClientModel client2 = new ClientModel();
            client2.setName("Google");
            client2.setEmail("contact@google.com");
            client2.setPhone("44999990002");
            client2.setCompany("Google");
            client2.setWorkspace(workspace);
            clientRepository.save(client2);

            ClientModel client3 = new ClientModel();
            client3.setName("Microsoft");
            client3.setEmail("contact@microsoft.com");
            client3.setPhone("44999990003");
            client3.setCompany("Microsoft");
            client3.setWorkspace(workspace);
            clientRepository.save(client3);

            ClientModel client4 = new ClientModel();
            client4.setName("Amazon");
            client4.setEmail("contact@amazon.com");
            client4.setPhone("44999990004");
            client4.setCompany("Amazon");
            client4.setWorkspace(workspace);
            clientRepository.save(client4);

            ClientModel client5 = new ClientModel();
            client5.setName("Netflix");
            client5.setEmail("contact@netflix.com");
            client5.setPhone("44999990005");
            client5.setCompany("Netflix");
            client5.setWorkspace(workspace);
            clientRepository.save(client5);

            ClientModel client6 = new ClientModel();
            client6.setName("Spotify");
            client6.setEmail("contact@spotify.com");
            client6.setPhone("44999990006");
            client6.setCompany("Spotify");
            client6.setWorkspace(workspace);
            clientRepository.save(client6);

            ClientModel client7 = new ClientModel();
            client7.setName("Tesla");
            client7.setEmail("contact@tesla.com");
            client7.setPhone("44999990007");
            client7.setCompany("Tesla");
            client7.setWorkspace(workspace);
            clientRepository.save(client7);

            ClientModel client8 = new ClientModel();
            client8.setName("Adobe");
            client8.setEmail("contact@adobe.com");
            client8.setPhone("44999990008");
            client8.setCompany("Adobe");
            client8.setWorkspace(workspace);
            clientRepository.save(client8);

            ClientModel client9 = new ClientModel();
            client9.setName("GitHub");
            client9.setEmail("contact@github.com");
            client9.setPhone("44999990009");
            client9.setCompany("GitHub");
            client9.setWorkspace(workspace);
            clientRepository.save(client9);

            ClientModel client10 = new ClientModel();
            client10.setName("Cloudflare");
            client10.setEmail("contact@cloudflare.com");
            client10.setPhone("44999990010");
            client10.setCompany("Cloudflare");
            client10.setWorkspace(workspace);
            clientRepository.save(client10);


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
                    .client(client1)
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
