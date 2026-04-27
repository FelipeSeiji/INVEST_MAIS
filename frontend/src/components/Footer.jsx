import './Footer.css';

export default function Footer() {
  return (
    <footer className="app-footer">
      <div className="footer-content">
        <div className="footer-section">
          <h3>Sobre Nós</h3>
          <p>
            O Invest+ é uma plataforma inovadora de gestão de investimentos desenvolvida para ajudar usuários a gerenciar seus portfólios de forma eficiente e segura. Utilizando tecnologias modernas como Spring Boot no backend e React no frontend, oferecemos ferramentas avançadas para análise de ativos, acompanhamento de aportes, controle de usuários e muito mais. Nosso objetivo é democratizar o acesso a informações financeiras precisas e facilitar decisões de investimento inteligentes.
          </p>  
        </div>
        <div className="footer-section">
          <h3>Contatos</h3>
          <p>
            email: ivestmais@gmail.com
          </p>
          <p>
            telefone: (11) 91234-5678
          </p>
        </div>
      </div>
      <div className="footer-bottom">
        <p>2026 Invest+</p>
      </div>
    </footer>
  );
}
