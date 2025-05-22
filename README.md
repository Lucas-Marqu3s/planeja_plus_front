# README - Frontend Android do Aplicativo de Controle Financeiro

Este documento contém instruções detalhadas para configurar e executar o frontend Android do aplicativo de controle financeiro desenvolvido com Kotlin.

## Tecnologias Utilizadas

- Kotlin
- Android SDK
- Retrofit para comunicação com API REST
- Material Design para interface de usuário
- Coroutines para operações assíncronas
- ViewBinding para manipulação de views

## Pré-requisitos

Para executar o frontend, você precisa ter instalado:

- Android Studio (versão mais recente recomendada)
- JDK 11 ou superior
- Emulador Android ou dispositivo físico com Android 6.0 (API 24) ou superior
- Backend do aplicativo em execução (via Docker)

## Configuração e Execução

### Passo 1: Importar o Projeto no Android Studio

1. **Abra o Android Studio**

2. **Selecione "Open an existing project"**

3. **Navegue até a pasta do frontend**
   ```
   caminho/para/app-financeiro/frontend
   ```

4. **Selecione a pasta e clique em "OK"**

5. **Aguarde o Android Studio importar e indexar o projeto**
   - Isso pode levar alguns minutos na primeira vez
   - O Gradle irá sincronizar e baixar as dependências necessárias

### Passo 2: Configurar a Conexão com o Backend

Antes de executar o aplicativo, verifique se o endereço do backend está configurado corretamente:

1. **Abra o arquivo `ApiClient.kt`**
   - Localizado em: `app/src/main/java/com/appfinanceiro/api/ApiClient.kt`

2. **Verifique a URL do backend**
   ```kotlin
   private val BASE_URL = "http://10.0.2.2:8080/"
   ```
   
   - Para emulador Android: mantenha `10.0.2.2` (endereço que aponta para o localhost da máquina host)
   - Para dispositivo físico: altere para o IP da sua máquina na rede local (ex: `192.168.1.100`)

### Passo 3: Executar o Aplicativo

1. **Conecte um dispositivo Android via USB ou inicie um emulador**
   - Para configurar um emulador:
     - Clique em "Tools" > "Device Manager"
     - Clique em "Create Device"
     - Selecione um telefone (como Pixel 4)
     - Escolha uma imagem do sistema (Android 10 ou superior recomendado)
     - Finalize a configuração

2. **Clique no botão "Run" (triângulo verde) na barra de ferramentas**

3. **Selecione o dispositivo/emulador e confirme**
   - O aplicativo será compilado e instalado no dispositivo

### Passo 4: Usar o Aplicativo

1. **Criar uma conta**
   - Na tela inicial, clique em "Cadastre-se"
   - Preencha os campos de nome, email e senha
   - Clique em "Cadastrar"

2. **Fazer login**
   - Use o email e senha cadastrados
   - Clique em "Entrar"

3. **Explorar as funcionalidades**
   - Dashboard: visualize seu saldo e transações recentes
   - Transações: gerencie suas entradas e saídas
   - Metas: defina e acompanhe suas metas financeiras
   - Perfil: edite seus dados e altere sua senha

## Estrutura do Projeto

O frontend segue uma arquitetura organizada:

- **api**: Cliente e serviços para comunicação com o backend
- **model**: Classes de dados
- **ui**: Activities, Fragments e Adapters para a interface do usuário
- **util**: Classes utilitárias como SessionManager

## Recursos Principais

### Autenticação
- Login com email e senha
- Registro de novos usuários
- Armazenamento seguro de token JWT

### Transações Financeiras
- Listagem de transações
- Criação de novas transações (receitas e despesas)
- Edição e exclusão de transações existentes

### Metas Financeiras
- Definição de metas com valor alvo e prazo
- Acompanhamento visual do progresso
- Gerenciamento de status (em andamento, concluída, cancelada)

### Perfil do Usuário
- Visualização e edição de dados pessoais
- Alteração de senha
- Logout seguro

## Solução de Problemas

### O aplicativo não compila
- Verifique se todas as dependências foram baixadas
- Execute "Build" > "Clean Project" e depois "Build" > "Rebuild Project"
- Verifique se o arquivo strings.xml contém todas as strings necessárias

### Erro de conexão com o backend
- Certifique-se de que o backend está em execução
- Verifique se o endereço IP no ApiClient.kt está correto
- Para dispositivos físicos, verifique se o dispositivo e o computador estão na mesma rede

### Problemas com o Gradle
- Atualize o Android Studio para a versão mais recente
- Tente "File" > "Invalidate Caches / Restart..."
- Verifique se o JDK está configurado corretamente

### Outros problemas
- Verifique os logs no Android Studio (Logcat)
- Certifique-se de que o dispositivo/emulador tem acesso à internet
- Verifique se as permissões de internet estão declaradas no AndroidManifest.xml

## Personalização

Você pode personalizar o aplicativo modificando:

- **Cores e temas**: em `res/values/colors.xml` e `res/values/themes.xml`
- **Textos**: em `res/values/strings.xml`
- **Layouts**: nos arquivos XML em `res/layout/`
- **Lógica de negócio**: nos arquivos Kotlin em `java/com/appfinanceiro/`

## Próximos Passos

Após executar o aplicativo com sucesso, você pode:
1. Adicionar novas funcionalidades
2. Melhorar a interface do usuário
3. Implementar testes automatizados
4. Adicionar suporte a múltiplos idiomas
