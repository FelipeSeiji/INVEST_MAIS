# Conformidade LGPD: Projeto Investe+

Este documento detalha as medidas técnicas e administrativas adotadas para garantir a proteção de dados pessoais e a conformidade com a Lei Geral de Proteção de Dados (Lei nº 13.709/2018).

---

## 1. Registro das Operações de Tratamento (ROPA)

### 1.1 Operação da Plataforma Investe+
| Processo | Papel | Finalidade | Base Legal | Categorias de Dados | Retenção |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Gestão Patrimonial** | Controlador | Cálculos de rentabilidade e rebalanceamento de carteira. | Art. 7º, V (Execução de Contrato) | Nome, E-mail, logs de acesso, aportes e ativos. | Enquanto a conta estiver ativa ou 5 anos após encerramento (exercício de direitos). |
| **Perfilamento** | Controlador | Personalização e recomendações conforme perfil de risco. | Art. 7º, V | Respostas qualitativas, metas de alocação por categoria. | Mesma da conta. |

---

## 2. Política de Privacidade e Direitos do Titular

*   **Agentes de Tratamento:** O projeto **Investe+** atua como Controlador dos dados.
*   **Dados Coletados:** Identificação (Nome, E-mail), Financeiros (Quantidades, Preços Médios), Perfil Qualitativo e Eletrônicos (IP, Logs).
*   **Direito ao Esquecimento:** O titular pode solicitar a exclusão definitiva. O sistema remove irreversivelmente o histórico financeiro e pessoal das bases de produção (Soft Delete não aplicado para dados pessoais).

---

## 3. Política de Segurança da Informação (PSI)

1.  **Criptografia em Repouso (Field-Level):** Implementação de cifragem **AES-256 GCM** via `AttributeEncryptor` para campos de identificação pessoal (`User.name` e `User.email`).
2.  **Hashing de Segurança:**
    *   **Senhas:** Protegidas com **Argon2** (padrão de alta resistência a força bruta).
    *   **Busca Segura:** Uso de **SHA-256** para `emailHash`, permitindo indexação sem expor o dado original.
    *   **Tokens de Reset:** Protegidos com **HMAC-SHA256**.
3.  **Criptografia em Trânsito:** Protocolos **TLS 1.2+** obrigatórios em todas as comunicações.
4.  **Autenticação de Dois Fatores (2FA):** Gerada via `SecureRandom` para garantir códigos imprevisíveis.
5.  **Gestão de Acessos (IAM):** Princípio do privilégio mínimo. O acesso aos dados financeiros é filtrado via `user_id` em nível de aplicação e persistência.

---

## 4. Relatório de Impacto à Proteção de Dados (RIPD)

*   **A) Identificação dos Agentes:** Controlador: Projeto Investe+ (MVP Repositório).
*   **B) Justificativa:** Tratamento de dados financeiros para automação de carteira de investimentos.
*   **C) Sistemas:** PostgreSQL (Produção), API Java 25 (Spring Boot 3.5), Frontend React.
*   **D) Dados Pessoais:** E-mail (Identificação); Financeiros (Saldos, Aportes); Perfil (Respostas de ativos).
*   **E) Fonte de Coleta:** Direta com o titular através de formulários cifrados.
*   **F) Medidas de Mitigação:** Cifragem **AES-256 GCM**, Hashing **Argon2**, Tokens **JWT** assinados e **2FA**.

---

## 5. Inventário de Dados Pessoais (IDP)

| ID | Campo | Natureza | Fonte | Armazenamento | Proteção |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **ASSET_001** | Nome Completo | Simples | Titular | PostgreSQL | **AES-256 GCM** |
| **ASSET_002** | E-mail Login | Simples | Titular | PostgreSQL | **AES-256 GCM** |
| **ASSET_003** | Senha | Crítico | Titular | PostgreSQL | **Argon2 Hashing** |
| **ASSET_004** | Perfil/Metas | Simples | Titular | PostgreSQL | Texto Claro / IAM |
| **ASSET_005** | Dados Financeiros | Crítico | Titular | PostgreSQL | Texto Claro / IAM* |
| **ASSET_006** | Logs de IP | Eletrônico | Sistema | Logback | Proteção de Acesso |

*\*Dados financeiros são mantidos em texto claro para permitir cálculos de rebalanceamento, sendo protegidos por rigoroso isolamento de identidade (IAM).*

---

## 6. Fluxo de Tráfego e Dados em Trânsito

<<<<<<< HEAD
| Dado Trafegado | Conteúdo | Proteção |
| :--- | :--- | :--- |
| Credenciais | E-mail e Senha | HTTPS/TLS |
| Token 2FA | Código de 6 dígitos | HTTPS/TLS |
| JSON Web Token (JWT) | Identidade e Escopos | Assinatura HS256 |
=======
| ID | Campo | Natureza | Fonte | Armazenamento |
| :--- | :--- | :--- | :--- | :--- |
| **ASSET_001** | E-mail Login | Simples | Titular | PostgreSQL (Cloud) |
| **ASSET_002** | Hash Senha | Simples | Titular | Argon2 Hash |
| **ASSET_003** | Perfil | Simples | Questionário | Tabela |
| **ASSET_004** | Aportes Financeiros | Simples/Crítico | Titular | AES-256 Encrypted |
| **ASSET_005** | Logs de IP/Acesso | Eletrônico | Sistema | Auditoria S3/Logback |

---

## 7. Fluxo de Tráfego e Dados em Trânsito

Para garantir a segurança ponta-a-ponta, o tráfego é segmentado em três fluxos principais, todos protegidos por TLS.

### 7.1. Fluxo de Entrada

Nesta etapa, os dados saem do navegador do usuário para o servidor.

| Dado Trafegado | Descrição do Conteúdo | Proteção em Trânsito |
| :--- | :--- | :--- |
| Credenciais de Acesso | E-mail e Senha. | HTTPS/TLS |
| Token 2FA/TOTP | Código de 6 dígitos gerado pelo app do usuário. | HTTPS/TLS |
| Dados Patrimoniais | Valores de aportes, nomes de ativos e datas de operação. | HTTPS/TLS |
| Respostas Suitability | Opções selecionadas no questionário de perfil de risco. | HTTPS/TLS |

### 7.2. Fluxo de Autenticação
Após a validação, a API devolve as chaves de acesso que trafegarão em todas as requisições.

| Dado Trafegado | Finalidade | Proteção / Mecanismo |
| :--- | :--- | :--- |
| JSON Web Token (JWT) | Contém o ID do usuário e permissões. | Assinado digitalmente. |
| Header de Autorização | Token enviado no cabeçalho. | HTTPS/TLS |

## 8. Boas Práticas

A estruturação deste documento visa consolidar a política de governança. Conforme a **Resolução CD/ANPD nº 4/2023**, a demonstração destas medidas (especialmente a implementação técnica de 2FA e Criptografia comprovada no código) pode reduzir eventuais sanções administrativas em até **40%**, sendo 20% pela política de boas práticas e 20% pelos mecanismos internos de mitigação.
>>>>>>> b61fe9efceb2c2d43e9efd578b66ce7316dcf3b7

---

## 7. Boas Práticas e Isenções

1.  **Natureza da Ferramenta:** O Investe+ é um sistema de auxílio ao investimento patrimonial e não constitui consultoria financeira.
2.  **Mitigação de Riscos:** Conforme a Resolução CD/ANPD nº 4/2023, a demonstração destas medidas técnicas pode reduzir sanções administrativas em caso de incidentes.
3.  **Responsabilidade do Usuário:** O titular é responsável pela guarda das credenciais 2FA e veracidade dos dados inseridos.

---

**Última Atualização:** 11 de Maio de 2026.
**Responsável:** Equipe de Desenvolvimento.
