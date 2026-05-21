import React, { useState, useEffect } from 'react';

const Aportes = () => {
    const [categories, setCategories] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    // Modal State
    const [modalConfig, setModalConfig] = useState({
        isOpen: false,
        type: 'category', // 'category' or 'asset'
        mode: 'create', // 'create' or 'edit'
        data: null,
        parentId: null // categoryId for assets
    });

    // Form State
    const [formData, setFormData] = useState({
        name: '',
        targetPercentage: '',
        ticker: '',
        currentPositionValue: '',
        quantity: '',
        averagePrice: ''
    });

    const token = localStorage.getItem('token');

    useEffect(() => {
        fetchData(true);
    }, []);

    const fetchData = async (isInitial = false) => {
        try {
            if (isInitial) setLoading(true);
            const response = await fetch('/api/assets/categories', {
                headers: { 'Authorization': `Bearer ${token}` }
            });
            if (!response.ok) throw new Error('Erro ao carregar dados');
            const data = await response.json();
            setCategories(data);
            setError(null);
        } catch (err) {
            setError(err.message);
        } finally {
            if (isInitial) setLoading(false);
        }
    };

    const handleOpenModal = (type, mode, data = null, parentId = null) => {
        setModalConfig({ isOpen: true, type, mode, data, parentId });
        if (mode === 'edit' && data) {
            setFormData({
                name: data.name || '',
                targetPercentage: data.targetPercentage || '',
                ticker: data.ticker || '',
                currentPositionValue: data.currentPositionValue || '',
                quantity: data.quantity || '',
                averagePrice: data.averagePrice || ''
            });
        } else {
            setFormData({
                name: '', targetPercentage: '', ticker: '',
                currentPositionValue: '', quantity: '', averagePrice: ''
            });
        }
    };

    const handleCloseModal = () => {
        setModalConfig({ ...modalConfig, isOpen: false });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        const { type, mode, data, parentId } = modalConfig;
        
        let url = type === 'category' ? '/api/assets/categories' : `/api/assets/categories/${parentId}/assets`;
        let method = mode === 'create' ? 'POST' : 'PUT';

        if (mode === 'edit') {
            url = type === 'category' ? `/api/assets/categories/${data.id}` : `/api/assets/${data.id}`;
        }

        const body = type === 'category' 
            ? { name: formData.name, targetPercentage: parseFloat(formData.targetPercentage) }
            : { 
                ticker: formData.ticker, 
                currentPositionValue: parseFloat(formData.currentPositionValue),
                quantity: parseFloat(formData.quantity),
                averagePrice: parseFloat(formData.averagePrice)
            };

        try {
            const response = await fetch(url, {
                method,
                headers: { 
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(body)
            });

            if (response.ok) {
                handleCloseModal();
                fetchData();
            } else {
                const errData = await response.json();
                alert(errData.detail || 'Erro na operação');
            }
        } catch (err) {
            alert('Erro de conexão');
        }
    };

    const handleDelete = async (type, id) => {
        if (!window.confirm(`Deseja realmente excluir este ${type === 'category' ? 'categoria' : 'ativo'}?`)) return;

        const url = type === 'category' ? `/api/assets/categories/${id}` : `/api/assets/${id}`;
        
        try {
            const response = await fetch(url, {
                method: 'DELETE',
                headers: { 'Authorization': `Bearer ${token}` }
            });
            if (response.ok) fetchData();
            else alert('Erro ao excluir');
        } catch (err) {
            alert('Erro de conexão');
        }
    };

    if (loading) {
        return (
            <div className="p-10 max-w-[1200px] mx-auto text-left flex justify-center items-center min-h-[50vh] font-sans">
                <div className="text-center text-text-muted font-medium animate-pulse">Carregando ativos...</div>
            </div>
        );
    }

    return (
        <div className="p-10 max-w-[1200px] mx-auto text-left animate-in fade-in slide-in-from-bottom-2 duration-500 font-sans">
            <header className="flex justify-between items-center mb-10">
                <div>
                    <h1 className="m-0 text-3xl font-extrabold tracking-tight bg-gradient-to-br from-text-main to-zinc-500 bg-clip-text text-transparent">Meus Ativos</h1>
                    <p className="text-sm text-text-muted mt-1 m-0">Gerencie sua estratégia e alocação</p>
                </div>
                <button 
                    className="bg-text-main text-white py-3 px-6 rounded-xl font-semibold flex items-center gap-2 transition-all duration-300 hover:shadow-md hover:-translate-y-0.5 active:scale-[0.98] cursor-pointer" 
                    onClick={() => handleOpenModal('category', 'create')}
                >
                    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 4v16m8-8H4"></path></svg>
                    Nova Categoria
                </button>
            </header>

            {error && <div className="mb-6 p-4 bg-red-50 text-red-500 rounded-xl border border-red-200">{error}</div>}

            <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                {categories.map(category => (
                    <div key={category.id} className="bg-bg-card border border-zinc-200/80 rounded-2xl p-6 shadow-sm hover:border-text-main hover:shadow-md transition-all duration-300 flex flex-col">
                        <div className="flex justify-between items-start mb-5">
                            <div className="flex flex-col items-start text-left">
                                <h2 className="m-0 text-xl font-bold text-zinc-900">{category.name}</h2>
                                <span className="bg-bg-primary text-text-muted py-1 px-3 rounded-full text-xs font-semibold mt-1.5 self-start uppercase tracking-wider">
                                    Alvo: {category.targetPercentage}%
                                </span>
                            </div>
                            <div className="flex gap-2">
                                <button className="bg-transparent p-2 cursor-pointer rounded-lg text-text-muted hover:bg-bg-primary hover:text-text-main transition-all duration-200" onClick={() => handleOpenModal('category', 'edit', category)}>
                                    <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z"></path></svg>
                                </button>
                                <button className="bg-transparent p-2 cursor-pointer rounded-lg text-text-muted hover:bg-red-50 hover:text-red-600 transition-all duration-200" onClick={() => handleDelete('category', category.id)}>
                                    <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path></svg>
                                </button>
                            </div>
                        </div>

                        <div className="mt-2.5 grow flex flex-col">
                            <div className="max-h-[320px] overflow-y-auto pr-2 mb-4 hover:scrollbar-thumb-zinc-300">
                                {category.assets && category.assets.length > 0 ? (
                                    category.assets.map(asset => (
                                        <div key={asset.id} className="flex justify-between items-center p-3 rounded-xl bg-zinc-50/70 mb-2 border border-transparent hover:border-zinc-200 transition-all duration-200">
                                            <div className="flex items-center gap-3">
                                                <div className="flex flex-col gap-1">
                                                    <div className="flex items-center gap-2">
                                                        <span className="font-bold text-text-main bg-white py-1 px-2.5 rounded-md shadow-[0_1px_2px_rgba(0,0,0,0.05)] text-sm">{asset.ticker}</span>
                                                        <span className={`text-xs font-semibold py-0.5 px-1.5 rounded ${asset.score > 0 ? 'bg-green-100 text-green-800' : asset.score < 0 ? 'bg-red-100 text-red-800' : 'bg-zinc-100 text-zinc-600'}`}>
                                                            {asset.score}
                                                        </span>
                                                    </div>
                                                    <div className="text-xs text-text-muted">
                                                        {asset.quantity} cotas • P.M. R$ {asset.averagePrice.toLocaleString('pt-BR')}
                                                    </div>
                                                </div>
                                            </div>
                                            <div className="flex items-center gap-3">
                                                <div className="text-right">
                                                    <div className="text-sm font-bold text-text-main">
                                                        R$ {asset.currentPositionValue.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}
                                                    </div>
                                                </div>
                                                <div className="flex gap-1">
                                                    <button className="bg-transparent p-1.5 cursor-pointer rounded-lg text-text-muted hover:bg-bg-primary hover:text-text-main transition-all duration-200" onClick={() => handleOpenModal('asset', 'edit', asset, category.id)}>
                                                        <svg className="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z"></path></svg>
                                                    </button>
                                                    <button className="bg-transparent p-1.5 cursor-pointer rounded-lg text-text-muted hover:bg-red-50 hover:text-red-600 transition-all duration-200" onClick={() => handleDelete('asset', asset.id)}>
                                                        <svg className="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path></svg>
                                                    </button>
                                                </div>
                                            </div>
                                        </div>
                                    ))
                                ) : (
                                    <div className="flex flex-col items-center justify-center py-8 text-text-muted">
                                        <svg className="w-8 h-8 mb-2 opacity-50" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="1.5" d="M20 12H4M12 20V4"></path></svg>
                                        <p className="text-sm m-0">Nenhum ativo nesta categoria.</p>
                                    </div>
                                )}
                            </div>
                            <button 
                                className="w-full bg-transparent border-2 border-dashed border-zinc-200 hover:border-text-main hover:bg-bg-primary text-text-muted hover:text-text-main py-3 rounded-xl mt-auto font-medium transition-all duration-200 active:scale-[0.99] flex items-center justify-center gap-2 cursor-pointer" 
                                onClick={() => handleOpenModal('asset', 'create', null, category.id)}
                            >
                                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 4v16m8-8H4"></path></svg>
                                Adicionar Ativo
                            </button>
                        </div>
                    </div>
                ))}

                {categories.length === 0 && (
                    <div className="text-center p-15 bg-bg-card rounded-3xl border border-dashed border-zinc-200 flex flex-col items-center justify-center" style={{ gridColumn: '1 / -1' }}>
                        <div className="text-5xl opacity-60 mb-4">📁</div>
                        <h3 className="text-lg font-bold text-text-main mb-1">Nenhuma categoria encontrada</h3>
                        <p className="max-w-[400px] text-sm text-text-muted mx-auto mb-6">Comece criando uma categoria para organizar seus investimentos (ex: Ações, FIIs, Renda Fixa).</p>
                        <button 
                            className="bg-text-main text-white py-3 px-6 rounded-xl font-semibold flex items-center gap-2 transition-all duration-300 hover:shadow-md hover:-translate-y-0.5 active:scale-[0.98] cursor-pointer mx-auto"
                            onClick={() => handleOpenModal('category', 'create')}
                        >
                            Criar Primeira Categoria
                        </button>
                    </div>
                )}
            </div>

            {modalConfig.isOpen && (
                <div className="fixed inset-0 bg-black/40 backdrop-blur-xs flex justify-center items-center z-[1000] animate-in fade-in duration-300" onClick={handleCloseModal}>
                    <div className="bg-bg-card p-8 rounded-3xl w-full max-w-[450px] shadow-2xl animate-in slide-in-from-bottom-5 duration-300" onClick={e => e.stopPropagation()}>
                        <div className="flex justify-between items-center mb-6">
                            <h2 className="m-0 text-2xl font-extrabold tracking-tight text-text-main">
                                {modalConfig.mode === 'create' ? 'Adicionar ' : 'Editar '}
                                {modalConfig.type === 'category' ? 'Categoria' : 'Ativo'}
                            </h2>
                            <button onClick={handleCloseModal} className="bg-transparent p-1 cursor-pointer rounded-lg text-text-muted hover:bg-bg-primary hover:text-text-main transition-all duration-200">
                                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M6 18L18 6M6 6l12 12"></path></svg>
                            </button>
                        </div>
                        
                        <form onSubmit={handleSubmit} className="flex flex-col gap-4">
                            {modalConfig.type === 'category' ? (
                                <>
                                    <div className="flex flex-col gap-1.5">
                                        <label className="text-xs font-semibold text-zinc-500 uppercase tracking-wider">Nome da Categoria</label>
                                        <input 
                                            type="text" 
                                            placeholder="Ex: AÇÕES BRASILEIRAS"
                                            value={formData.name}
                                            onChange={e => setFormData({...formData, name: e.target.value.toUpperCase()})}
                                            required
                                            className="w-full px-4 py-3 rounded-xl bg-zinc-50/50 border border-zinc-200 text-zinc-900 placeholder-zinc-400 focus:outline-none focus:border-zinc-900 focus:ring-1 focus:ring-zinc-900 transition-all duration-200 m-0"
                                        />
                                    </div>
                                    <div className="flex flex-col gap-1.5">
                                        <label className="text-xs font-semibold text-zinc-500 uppercase tracking-wider">Porcentagem Alvo (%)</label>
                                        <input 
                                            type="number" 
                                            placeholder="Ex: 25"
                                            value={formData.targetPercentage}
                                            onChange={e => setFormData({...formData, targetPercentage: e.target.value})}
                                            required
                                            className="w-full px-4 py-3 rounded-xl bg-zinc-50/50 border border-zinc-200 text-zinc-900 placeholder-zinc-400 focus:outline-none focus:border-zinc-900 focus:ring-1 focus:ring-zinc-900 transition-all duration-200 m-0"
                                        />
                                    </div>
                                </>
                            ) : (
                                <>
                                    <div className="flex flex-col gap-1.5">
                                        <label className="text-xs font-semibold text-zinc-500 uppercase tracking-wider">Ticker (Código)</label>
                                        <input 
                                            type="text" 
                                            placeholder="Ex: PETR4"
                                            value={formData.ticker}
                                            onChange={e => setFormData({...formData, ticker: e.target.value.toUpperCase()})}
                                            required
                                            className="w-full px-4 py-3 rounded-xl bg-zinc-50/50 border border-zinc-200 text-zinc-900 placeholder-zinc-400 focus:outline-none focus:border-zinc-900 focus:ring-1 focus:ring-zinc-900 transition-all duration-200 m-0"
                                        />
                                    </div>
                                    <div className="flex flex-col gap-1.5">
                                        <label className="text-xs font-semibold text-zinc-500 uppercase tracking-wider">Valor Atual da Posição (R$)</label>
                                        <input 
                                            type="number" 
                                            step="0.01"
                                            placeholder="0.00"
                                            value={formData.currentPositionValue}
                                            onChange={e => setFormData({...formData, currentPositionValue: e.target.value})}
                                            required
                                            className="w-full px-4 py-3 rounded-xl bg-zinc-50/50 border border-zinc-200 text-zinc-900 placeholder-zinc-400 focus:outline-none focus:border-zinc-900 focus:ring-1 focus:ring-zinc-900 transition-all duration-200 m-0"
                                        />
                                    </div>
                                    <div className="grid grid-cols-2 gap-4">
                                        <div className="flex flex-col gap-1.5">
                                            <label className="text-xs font-semibold text-zinc-500 uppercase tracking-wider">Quantidade</label>
                                            <input 
                                                type="number" 
                                                step="0.0001"
                                                placeholder="0"
                                                value={formData.quantity}
                                                onChange={e => setFormData({...formData, quantity: e.target.value})}
                                                required
                                                className="w-full px-4 py-3 rounded-xl bg-zinc-50/50 border border-zinc-200 text-zinc-900 placeholder-zinc-400 focus:outline-none focus:border-zinc-900 focus:ring-1 focus:ring-zinc-900 transition-all duration-200 m-0"
                                            />
                                        </div>
                                        <div className="flex flex-col gap-1.5">
                                            <label className="text-xs font-semibold text-zinc-500 uppercase tracking-wider">Preço Médio (R$)</label>
                                            <input 
                                                type="number" 
                                                step="0.01"
                                                placeholder="0.00"
                                                value={formData.averagePrice}
                                                onChange={e => setFormData({...formData, averagePrice: e.target.value})}
                                                required
                                                className="w-full px-4 py-3 rounded-xl bg-zinc-50/50 border border-zinc-200 text-zinc-900 placeholder-zinc-400 focus:outline-none focus:border-zinc-900 focus:ring-1 focus:ring-zinc-900 transition-all duration-200 m-0"
                                            />
                                        </div>
                                    </div>
                                </>
                            )}

                            <div className="flex gap-3 justify-end mt-4">
                                <button type="button" className="px-6 py-2.5 rounded-xl font-semibold text-sm bg-bg-primary hover:bg-zinc-200 text-text-main cursor-pointer transition-all duration-200 active:scale-[0.98]" onClick={handleCloseModal}>Cancelar</button>
                                <button type="submit" className="px-6 py-2.5 rounded-xl font-semibold text-sm bg-text-main hover:bg-zinc-800 text-white cursor-pointer transition-all duration-300 hover:shadow-md hover:-translate-y-0.5 active:scale-[0.98]">Salvar</button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
};

export default Aportes;
