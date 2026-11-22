\# TRACK FIX - Sistema de Gestão de Assistência Técnica



Aplicação desktop desenvolvida em \*\*Java (Swing + JDBC)\*\* com persistência em \*\*MySQL\*\* para o gerenciamento completo de uma assistência técnica. O sistema controla o cadastro de clientes, estoque de peças, usuários e todo o fluxo operacional de \*\*Ordens de Serviço (OS)\*\*.



\---



\## Funcionalidades



\- \*\*Autenticação e Segurança:\*\* Login com verificação de status ativo e criptografia de senhas utilizando hash \*\*SHA-256\*\*.

\- \*\*Gestão de Clientes:\*\* Cadastro, consulta, edição e exclusão de clientes.

\- \*\*Controle de Estoque (Peças):\*\* Gerenciamento de peças com baixa automática ao vincular itens a uma OS e emissão de alertas ao atingir o estoque mínimo.

\- \*\*Fluxo de Ordens de Serviço:\*\*

&#x20; - Abertura de OS vinculada a cliente e técnico responsável.

&#x20; - Adição de peças utilizadas com recálculo automático do valor total (mão de obra + peças).

&#x20; - Registro de diagnóstico técnico, conclusão e cancelamento de OS.

\- \*\*Sincronização entre Telas:\*\* Atualização automática das listagens e alertas na interface gráfica sempre que ocorrem alterações nos dados.



\---



\## Arquitetura e Padrões de Projeto



O projeto foi estruturado na arquitetura \*\*MVC (Model-View-Controller)\*\* combinada com \*\*DAO (Data Access Object)\*\* para isolamento das consultas SQL, além da aplicação de três padrões de projeto:



\- \*\*Singleton (`AssistenciaFacade`):\*\* Garante uma única instância centralizada da fachada de negócios durante toda a execução da aplicação.

\- \*\*Facade (`AssistenciaFacade`):\*\* Fornece uma interface unificada para a camada `View`, encapsulando a comunicação entre os controladores (`UsuarioController`, `ClienteController`, `PecaController` e `OrdemServicoController`).

\- \*\*Observer (`SistemaObserver`):\*\* Permite que as telas (`TelaMenu`, `CRUDOrdemServico`, `CRUDPeca` e `CRUDCliente`) se inscrevam como observadoras e sejam notificadas automaticamente após operações de escrita ou alertas de estoque.



\---



\## Tecnologias Utilizadas



\- \*\*Linguagem:\*\* Java (JDK 17+)

\- \*\*Interface Gráfica:\*\* Java Swing

\- \*\*Banco de Dados:\*\* MySQL (`bd\_assistencia`)

\- \*\*Conectividade:\*\* JDBC (`lib/conn.jar`)



\---



\## Estrutura de Diretórios



```text

├── lib/

│   └── conn.jar                     # Driver JDBC do MySQL

├── src/

│   ├── Main.java                    # Ponto de entrada da aplicação

│   ├── controller/

│   │   ├── AssistenciaFacade.java   # Singleton + Facade + Subject (Observer)

│   │   ├── SistemaObserver.java     # Interface Observer

│   │   ├── UsuarioController.java

│   │   ├── ClienteController.java

│   │   ├── PecaController.java

│   │   └── OrdemServicoController.java

│   ├── dao/

│   │   ├── Conexao.java             # Gerenciamento de conexão JDBC

│   │   ├── UsuarioDAO.java

│   │   ├── ClienteDAO.java

│   │   ├── PecaDAO.java

│   │   └── OrdemServicoDAO.java

│   ├── model/

│   │   ├── Usuario.java

│   │   ├── Cliente.java

│   │   ├── Peca.java

│   │   ├── OrdemServico.java

│   │   └── ItemOrdemServico.java

│   ├── util/

│   │   └── ResultadoCadastro.java   # Enum de retorno das operações

│   └── view/

│       ├── TelaLogin.java

│       ├── TelaMenu.java

│       ├── CRUDCliente.java

│       ├── CRUDPeca.java

│       ├── CRUDOrdemServico.java

│       └── FormularioOrdemServico.java

└── bin/                             # Arquivos compilados (.class)

```



\---



\## Como Configurar e Executar



\### 1. Banco de Dados

1\. Certifique-se de que o serviço do \*\*MySQL\*\* esteja em execução na porta `3306`.

2\. Abra o \*\*MySQL Workbench\*\* (ou outro cliente SQL) e execute o script de criação do banco `bd\_assistencia` e suas tabelas (`usuario`, `cliente`, `peca`, `ordem\_servico`, `item\_ordem\_servico`).



\### 2. Configuração da Conexão

Abra o arquivo `src/dao/Conexao.java` e ajuste as credenciais conforme o seu ambiente local:



```java

private static final String URL = "jdbc:mysql://localhost:3306/bd\_assistencia";

private static final String USUARIO = "root";

private static final String SENHA = "sua\_senha\_aqui";

```



\### 3. Execução do Projeto



\*\*Via VS Code (Java Extension Pack):\*\*

1\. Adicione `lib/conn.jar` em \*\*JAVA PROJECTS > Referenced Libraries\*\*.

2\. Abra `src/Main.java` e clique em \*\*Run Java\*\*.



\*\*Via Terminal (com JDK configurado no `PATH`):\*\*

```bash

javac -encoding UTF-8 -d bin -cp "lib/conn.jar" src/\*.java src/model/\*.java src/dao/\*.java src/controller/\*.java src/util/\*.java src/view/\*.java

java -cp "bin;lib/conn.jar" Main

```



\---



\## Acesso Inicial



Após executar o script SQL inicial, utilize as seguintes credenciais para acessar o sistema:



\- \*\*Usuário:\*\* `admin`

\- \*\*Senha:\*\* `123`

