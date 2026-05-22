export default function Footer() {
  return (
    <footer className="w-full bg-bg-primary border-t border-zinc-200 py-10 px-10 pb-5 font-sans mt-auto">
      <div className="max-w-[1000px] mx-auto mb-8 flex flex-wrap gap-10 text-left">
        <div className="flex-1 min-w-[250px]">
          <h3 className="text-base font-semibold text-text-main mb-3">Sobre Nós</h3>
          <p className="text-sm leading-relaxed text-text-muted mb-2 text-justify">
            O Invest+ é uma plataforma inovadora de gestão de investimentos desenvolvida para ajudar usuários a gerenciar seus portfólios de forma eficiente e segura. Utilizando tecnologias modernas como Spring Boot no backend e React no frontend, oferecemos ferramentas avançadas para análise de ativos, acompanhamento de aportes, controle de usuários e muito mais. Nosso objetivo é democratizar o acesso a informações financeiras precisas e facilitar decisões de investimento inteligentes.
          </p>
        </div>
        <div className="flex-1 min-w-[250px]">
          <h3 className="text-base font-semibold text-text-main mb-3">Contatos</h3>
          <p className="text-sm leading-relaxed text-text-muted mb-2 text-justify">
            email: investmaisinfo@gmail.com
          </p>
          <p className="text-sm leading-relaxed text-text-muted mb-2 text-justify">
            telefone: (11) 96387-2730
          </p>
        </div>
      </div>
      <div className="text-center pt-5 border-t border-zinc-200 text-xs text-zinc-400">
        <p>2026 Invest+</p>
      </div>
    </footer>
  );
}
