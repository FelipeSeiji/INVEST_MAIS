# Conformidade LGPD: Projeto Investe+

Este documento detalha as medidas técnicas e administrativas adotadas para garantir a proteção de dados pessoais e a conformidade com a Lei Geral de Proteção de Dados (Lei nº 13.709/2018).

---

## 1. Registro das Operações de Tratamento (ROPA)

### 1.1 Operação da Plataforma Investe+
| Processo | Papel | Finalidade | Base Legal | Categorias de Dados | Retenção |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Gestão Patrimonial** | Controlador | Cálculos de rentabilidade e rebalanceamento de carteira. | Art. 7º, V (Execução de Contrato) | Nome, E-mail, logs de acesso, aportes e ativos. | Enquanto a conta estiver ativa. |
| **Perfilamento** | Controlador | Personalização e recomendações conforme perfil de risco. | Art. 7º, V | Respostas qualitativas, metas de alocação por categoria. | Enquanto a conta estiver ativa. |

---

## 2. Política de Privacidade e Direitos do Titular

*   **Agentes de Tratamento:** O projeto **Investe+** atua como Controlador dos dados.
*   **Dados Coletados:** Identificação (Nome, E-mail), Financeiros (Quantidades, Preços Médios), Perfil Qualitativo e Eletrônicos (IP, Logs).
*   **Direito ao Esquecimento:** O titular pode solicitar a exclusão definitiva. O sistema remove irreversivelmente o histórico financeiro e pessoal das bases de produção.

---

## 3. Política de Segurança da Informação (PSI)

1.  **Criptografia em Repouso (Field-Level):** Implementação de cifragem **AES-256 GCM** via `AttributeEncryptor` focada em dados de identificação pessoal (`User.name` e `User.email`). Dados financeiros são protegidos por isolamento lógico.
2.  **Hashing de Segurança:**
    *   **Senhas:** Protegidas com **Argon2** (padrão de alta resistência a força bruta).
    *   **Busca Segura:** Uso de **SHA-256** para `emailHash`, permitindo indexação sem expor o dado original.
    *   **Tokens de Reset:** Protegidos com **HMAC-SHA256**.
3.  **Criptografia em Trânsito:** Protocolos **TLS 1.2+** obrigatórios em todas as comunicações via HTTPS.
4.  **Autenticação de Dois Fatores (2FA):** Gerada via `SecureRandom` e armazenada com criptografia para garantir códigos imprevisíveis.
5.  **Gestão de Acessos (IAM):** Princípio do privilégio mínimo. O acesso aos dados financeiros é filtrado via `user_id` em nível de aplicação e persistência.

---

## 4. Relatório de Impacto à Proteção de Dados (RIPD)

*   **A) Identificação dos Agentes:** Controlador: Projeto Investe+ (Equipe de Desenvolvimento).
*   **B) Justificativa:** Tratamento de dados financeiros para automação de carteira de investimentos.
*   **C) Sistemas:** H2 Database (In-Memory/MVP), API Java 25 (Spring Boot 3.5), Frontend React (Vite).
*   **D) Dados Pessoais:** E-mail (Identificação); Financeiros (Saldos, Aportes); Perfil (Respostas de ativos).
*   **E) Fonte de Coleta:** Direta com o titular através de formulários cifrados.
*   **F) Medidas de Mitigação:** Cifragem **AES-256 GCM**, Hashing **Argon2**, Tokens **JWT** assinados e **2FA**.

---

## 5. Inventário de Dados Pessoais (IDP)

| ID | Campo | Natureza | Fonte | Armazenamento | Proteção |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **ASSET_001** | Nome Completo | Simples | Titular | H2 | **AES-256 GCM** |
| **ASSET_002** | E-mail Login | Simples | Titular | H2 | **AES-256 GCM** |
| **ASSET_003** | Senha | Crítico | Titular | H2 | **Argon2 Hashing** |
| **ASSET_004** | Perfil/Metas | Simples | Titular | H2 | Texto Claro / IAM |
| **ASSET_005** | Dados Financeiros | Crítico | Titular | H2 | Texto Claro / IAM* |
| **ASSET_006** | Logs de IP/Acesso | Eletrônico | Sistema | Logback / Arquivos | Proteção de Acesso |

*\*Dados financeiros são mantidos em texto claro no banco para viabilizar cálculos de performance, sendo protegidos por rigoroso isolamento de identidade (IAM) em nível de linha.*

---

## 6. Tecnologias de Armazenamento (Cookies e Similares)

O sistema utiliza o armazenamento local do navegador para funções críticas de segurança e usabilidade. Não utilizamos cookies de rastreamento de terceiros (Analytics/Ads).

| Nome | Fornecedor | Finalidade | Categoria | Expiração |
| :--- | :--- | :--- | :--- | :--- |
| `token` | Próprio | Armazena o JSON Web Token (JWT) para manter a sessão ativa. | Estritamente Necessário (LocalStorage) | Até o logout ou limpeza do cache. |
| `theme` | Próprio | Armazena a preferência de tema (claro/escuro). | Preferências (LocalStorage) | Persistente. |

---

## 7. Fluxo de Tráfego e Dados em Trânsito

Para garantir a segurança ponta-a-ponta, o tráfego é segmentado em fluxos protegidos por TLS 1.2+.

### 7.1. Fluxo de Entrada e Autenticação
| Dado Trafegado | Descrição / Finalidade | Proteção em Trânsito |
| :--- | :--- | :--- |
| Credenciais | E-mail e Senha enviados para login. | HTTPS / TLS |
| Token 2FA/TOTP | Código de 6 dígitos para validação extra. | HTTPS / TLS |
| **JWT (Token)** | Token de acesso devolvido após login e enviado em cada requisição. | Assinatura HS256 / HTTPS |
| Dados Patrimoniais | Valores de aportes e nomes de ativos. | HTTPS / TLS |

---

## 8. Boas Práticas e Isenções

1.  **Natureza da Ferramenta:** O Investe+ é um sistema de auxílio ao investimento patrimonial e não constitui consultoria financeira.
2.  **Mitigação de Riscos:** Conforme a **Resolução CD/ANPD nº 4/2023**, a demonstração destas medidas técnicas (2FA, Criptografia GCM, JWT) pode reduzir eventuais sanções administrativas.
3.  **Responsabilidade do Usuário:** O titular é responsável pela guarda das credenciais 2FA e veracidade dos dados inseridos.
4.  **Marco Civil da Internet:** Logs de acesso são mantidos por 6 meses para cumprimento de obrigação legal (Art. 15 da Lei 12.965/2014).

---

**Última Atualização:** 12 de Maio de 2026.
**Responsável:** Equipe de Desenvolvimento.
