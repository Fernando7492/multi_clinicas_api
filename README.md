# 🏥 Sistema de Agendamento Multi-Tenant para Clínicas (SaaS)

## 1. Visão Geral do Produto
Este projeto consiste no desenvolvimento de uma plataforma **SaaS (Software as a Service) Multi-Tenant** voltada para a gestão de agendamentos médicos. O objetivo é fornecer a clínicas de pequeno e médio porte uma solução digital para expor suas agendas, permitir que pacientes marquem consultas online e reduzir o absenteísmo (*no-show*) através de notificações automáticas.

Diferente de um software instalado localmente, esta plataforma permite que múltiplas clínicas utilizem o mesmo software, onde cada uma possui seu ambiente isolado e personalizado via subdomínio (ex: `clinica-vida.sistema.com` e `cardio-center.sistema.com`).

---

## 2. Arquitetura do Sistema

### 2.1 Modelo Multi-Tenant
O sistema adota uma estratégia de **Banco de Dados Compartilhado (Shared Database)** com isolamento lógico.

* **Identificação do Tenant:** O sistema identifica qual clínica está sendo acessada através da URL (Subdomínio).
* **Isolamento de Dados:** Todas as consultas ao banco de dados filtram obrigatoriamente pelo `clinic_id`.
* **Escalabilidade:** Permite a adição de novas clínicas sem necessidade de provisionar nova infraestrutura.

### 2.2 Stack Tecnológica (MVP)
* **Frontend:** React com Next.js.
* **Backend:** Java com Spring Boot.
* **Banco de Dados:** PostgreSQL ou MySQL (Relacional).
* **Mensageria/Jobs:** Redis (para filas de e-mail).

---

## 3. Atores do Sistema

| Ator | Descrição | Permissões Chave |
| :--- | :--- | :--- |
| **Visitante** | Usuário não autenticado acessando o portal da clínica. | Visualizar médicos, especialidades e horários livres. |
| **Paciente** | Usuário final que consome o serviço médico. | Agendar, visualizar histórico e cancelar consultas. |
| **Recepcionista** | Funcionário da clínica (Operacional). | Gerenciar médicos, grades de horário e visualizar agenda completa. |
| **Admin da Clínica** | Gestor da unidade (Gerencial). | Cadastrar recepcionistas e gerenciar dados da clínica. |

---

## 4. Funcionalidades por Módulo

### 4.1 Módulo Público (Agendamento)
Focado na conversão e usabilidade para o paciente.

* **Catálogo de Especialidades:** Listagem das áreas médicas atendidas pela clínica.
* **Busca de Médicos:** Listagem de profissionais filtrada por especialidade.
* **Calendário de Disponibilidade:** Visualização intuitiva dos slots livres (ex: 08:00, 08:30).
* **Fluxo de Agendamento Híbrido:**
    * Permite iniciar a escolha do horário como visitante.
    * Exige Login ou Cadastro rápido (Nome, CPF, E-mail, Tel) apenas no momento de confirmar a reserva.

### 4.2 Módulo do Paciente (Área Logada)
Focado na autogestão.

* **Meus Agendamentos:** Lista de consultas futuras e passadas.
* **Cancelamento:** Botão para cancelar consultas futuras (libera o horário na hora).
* **Segurança:** O cancelamento exige autenticação para evitar fraudes ou erros.

### 4.3 Módulo Administrativo (Backoffice da Clínica)
Painel de controle para a equipe interna.

* **Gestão de Corpo Clínico:** Cadastro de médicos (Nome, CRM, Foto).
* **Gestão de Grade Horária:** Definição dos blocos de trabalho (ex: Dr. João atende Segundas das 08h às 12h).
* **Bloqueio de Agenda:** Capacidade de bloquear horários manualmente (férias, feriados).
* **Gestão de Usuários:** Admin pode criar contas para novos recepcionistas.

### 4.4 Módulo de Notificações (Automático)
Serviço de background para garantir o comparecimento.

* **E-mail de Confirmação:** Disparado imediatamente após o sucesso do agendamento (`Status: Agendado`).
* **E-mail de Lembrete:** Disparado automaticamente 24 horas antes do horário da consulta (`Cron Job`).

---

## 5. Modelo de Dados (Entidades Principais e Atributos)
Abaixo estão listadas as entidades do banco de dados.
*Nota: `PK` = Chave Primária, `FK` = Chave Estrangeira.*

### 5.1 Tabela Global
* **Clinicas (Tenants)**
    * `id` (PK): UUID ou Long.
    * `nome_fantasia`: String (Ex: "Clínica Santa Vida").
    * `subdominio`: String (Unique) (Ex: "santa-vida"). Identificador chave para o multi-tenant.
    * `ativo`: Boolean (Para desativar inadimplentes).
    * `created_at`: Timestamp.

### 5.2 Tabelas por Tenant (Todas possuem `clinic_id`)

* **Usuarios_Admin (Equipe da Clínica)**
    * `id` (PK).
    * `clinic_id` (FK).
    * `nome`: String.
    * `email`: String (Login).
    * `senha_hash`: String.
    * `role`: Enum (ADMIN, RECEPCIONISTA).

* **Pacientes**
    * `id` (PK).
    * `clinic_id` (FK).
    * `nome`: String.
    * `cpf`: String (Geralmente Unique dentro do Tenant).
    * `email`: String.
    * `telefone`: String.
    * `senha_hash`: String.

* **Medicos**
    * `id` (PK).
    * `clinic_id` (FK).
    * `nome`: String.
    * `crm`: String.
    * `ativo`: Boolean (Permite "desligar" médico sem apagar histórico).

* **Especialidades**
    * `id` (PK).
    * `clinic_id` (FK).
    * `nome`: String (Ex: "Cardiologia").

* **Medico_Especialidade (Tabela Associativa)**
    * `medico_id` (FK).
    * `especialidade_id` (FK).

* **Grades_Horario (Configuração de Agenda)**
    * `id` (PK).
    * `medico_id` (FK).
    * `dia_semana`: Integer (0=Dom, 1=Seg, ... 6=Sab).
    * `hora_inicio`: Time (Ex: 08:00).
    * `hora_fim`: Time (Ex: 12:00).
    * `duracao_consulta`: Integer (Minutos, opcional se for padrão da clínica).

* **Agendamentos (O Core do Sistema)**
    * `id` (PK).
    * `clinic_id` (FK).
    * `paciente_id` (FK).
    * `medico_id` (FK).
    * `data_consulta`: Date/Timestamp.
    * `hora_inicio`: Time.
    * `hora_fim`: Time.
    * `status`: Enum (AGENDADO, CANCELADO_PACIENTE, CANCELADO_CLINICA, REALIZADO).
    * `observacoes`: Text.
    * `created_at`: Timestamp.

---

## 6. Regras de Negócio Críticas (MVP)

1.  **Unicidade de Horário:** O sistema não pode permitir dois agendamentos para o mesmo médico no mesmo horário (controle de concorrência).
2.  **Isolamento de Dados:** Um paciente da Clínica A não pode ver médicos da Clínica B sob nenhuma circunstância.
3.  **Política de Cancelamento:** O paciente só pode cancelar consultas pelo site.
4.  **Cadastro Simplificado:** O fluxo de cadastro do paciente deve ser integrado ao agendamento para reduzir atrito.

---

## 7. Guia de Execução da API

Este guia descreve como configurar e executar a API localmente.

### 7.1 Pré-requisitos

*   **Java 17** ou superior.
*   **Maven** 3.8+.
*   **PostgreSQL** (para execução padrão).

### 7.2 Configuração do Banco de Dados

Por padrão, a aplicação espera um banco de dados PostgreSQL rodando em `localhost:5432`.

1.  Crie um banco de dados chamado `clinicas_db`.
2.  Certifique-se de que as credenciais no `application.yaml` (ou variáveis de ambiente) estejam corretas.
    *   Usuário padrão: `spring`
    *   Senha padrão: `123`

### 7.3 Como Executar

Na raiz do projeto (pasta `api`), execute:

```bash
mvn spring-boot:run
```

A aplicação iniciará na porta `8080` com o context path `/api`.

### 7.4 Executando Testes

Para rodar os testes (que utilizam banco H2 em memória):

```bash
mvn test
```
