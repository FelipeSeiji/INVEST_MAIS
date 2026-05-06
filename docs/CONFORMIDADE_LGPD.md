# Conformidade LGPD: Projeto Investe+


## 1. Registro das Operações de Tratamento (ROPA)

### 1.1 Operação da Plataforma Investe+ (Foco Técnico)
| Processo | Papel | Finalidade | Base Legal | Categorias de Dados | Retenção |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Gestão Patrimonial** | Controlador | Cálculos de rentabilidade e rebalanceamento de carteira. | Art. 7º, V (Execução de Contrato) | E-mail, logs de acesso, aportes financeiros, ativos em custódia. | Enquanto a conta estiver ativa ou 5 anos após encerramento (exercício de direitos). |
| **Perfilamento** | Controlador | Personalização e recomendações conforme perfil de risco. | Art. 7º, V | Respostas ao questionário de perfil, nível de tolerância a risco. | Mesma da conta. |

### 1.2 Governança Institucional (RH e Administrativo)
| Processo | Papel | Finalidade | Base Legal | Categorias de Dados | Medida de Segurança |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Gestão de RH** | Controlador | Obrigações trabalhistas e previdenciárias. | Art. 7º, II e V | Identificação, Dados Bancários, Filiação Sindical (Sensível). | RBAC e Criptografia de documentos. |

---

## 2. Política de Privacidade e Direitos do Titular

*   **Agentes de Tratamento:** O projeto **Investe+** atua como Controlador dos dados.
*   **Dados Coletados:** Identificação (Nome, CPF, E-mail), Financeiros (Aportes, Saldos), Perfil e Eletrônicos (IP, Logs).
*   **Direito ao Esquecimento:** O titular pode solicitar a exclusão definitiva. O sistema executa a exclusão, removendo irreversivelmente o histórico financeiro e pessoal das bases de produção.

---

## 3. Política de Segurança da Informação (PSI)

1.  **Criptografia em Repouso:** Implementação de cifragem **AES-256 GCM** via `AttributeEncryptor` para campos sensíveis no banco de dados.
2.  **Criptografia em Trânsito:** Protocolos **TLS 1.2+** obrigatórios em todas as comunicações API/Frontend.
3.  **Autenticação de Dois Fatores (2FA):** Uso obrigatório de segundo fator (TOTP/E-mail) para acesso à conta e operações críticas.
4.  **Gestão de Acessos (IAM):** Princípio do privilégio mínimo (*Least Privilege*), garantindo que usuários acessem apenas seus próprios dados.
5.  **Auditoria:** Geração de logs imutáveis para todas as tentativas de login e alterações de perfil.

---

## 4. Relatório de Impacto à Proteção de Dados (RIPD)
*Elaborado seguindo os 15 requisitos mínimos da ANPD para o projeto Investe+.*

*   **A) Identificação dos Agentes:** Controlador: Projeto Investe+ (MVP Repositório).
*   **B) Partes Interessadas:** Consultadas equipes de TI (Desenvolvimento Fullstack), Jurídico e usuários beta da plataforma.
*   **C) Justificativa:** Tratamento de dados financeiros sensíveis e perfilamento de investimento que impactam a vida financeira do titular.
*   **D) Projeto/Processo:** Plataforma Investe+ para gestão patrimonial e rebalanceamento automatizado de ativos.
*   **E) Sistemas:** Banco de Dados PostgreSQL (Produção) / H2 (Testes), API Java 25 com Spring Boot 3.5, Interface SPA em React.
*   **F) Descrição do Tratamento:** Ciclo completo: Cadastro -> Inserção manual de ativos/aportes -> Processamento de rentabilidade -> Exclusão definitiva.
*   **G) Dados Pessoais/Sensíveis:** Identificação (E-mail); Financeiros (Saldos, Aportes, Valor de Ativos); Perfil.
*   **H) Categorias de Titulares:** Investidores individuais cadastrados na plataforma.
*   **I) Grupos Vulneráveis:** Não há tratamento de dados de menores ou grupos vulneráveis.
*   **J) Volume e Escala:** Tratamento focado em registros financeiros individuais, com escalabilidade prevista via Cloud.
*   **K) Fonte de Coleta:** Direta com o titular através de formulários criptografados no frontend.
*   **L) Finalidade:** Automatização de cálculos de carteira e sugestão de rebalanceamento conforme perfil de risco.
*   **M) Compartilhamentos:** Nenhum.
*   **N) Hipótese Legal & Princípios:** Baseado no Art. 7º, V (Execução de Contrato). Aplicação dos princípios de finalidade, necessidade e segurança.
*   **O) Medidas de Mitigação:** Autenticação **2FA**, Cifragem **AES-256 GCM** via, Hashing **Argon2** para senhas, tokens **JWT** e comunicação **SSL/TLS**.

---

## 5. Termos de Uso e Isenções Legais

1.  **Natureza da Ferramenta:** O Investe+ é um sistema de auxílio ao investimento patrimonial.
2.  **Isenção de Consultoria:** A plataforma **não constitui consultoria financeira**, recomendação de investimento ou análise profissional de valores mobiliários.
3.  **Responsabilidade do Usuário:** O usuário é integralmente responsável pela veracidade dos dados inseridos e pela guarda das credenciais 2FA.
4.  **Rescisão:** A exclusão da conta pelo usuário implica na remoção imediata de todos os ativos associados.

---

## 6. Inventário de Dados Pessoais (IDP)

| ID | Campo | Natureza | Fonte | Armazenamento |
| :--- | :--- | :--- | :--- | :--- |
| **ASSET_001** | E-mail Login | Simples | Titular | PostgreSQL (Cloud) |
| **ASSET_002** | Hash Senha | Simples | Titular | Argon2 Hash |
| **ASSET_003** | Perfil | Simples | Questionário | Tabela |
| **ASSET_004** | Aportes Financeiros | Simples/Crítico | Titular | AES-256 Encrypted |
| **ASSET_005** | Logs de IP/Acesso | Eletrônico | Sistema | Auditoria S3/Logback |

---

## 7. Boas Práticas

A estruturação deste documento visa consolidar a política de governança. Conforme a **Resolução CD/ANPD nº 4/2023**, a demonstração destas medidas (especialmente a implementação técnica de 2FA e Criptografia comprovada no código) pode reduzir eventuais sanções administrativas em até **40%**, sendo 20% pela política de boas práticas e 20% pelos mecanismos internos de mitigação.

---
**Última Atualização:** 05 de Maio de 2026.
**Responsável:** Equipe de Desenvolvimento.
