import React, { useState, useEffect } from 'react';
import './Aportes.css';

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

    if (loading) return <div className="flex-1 flex items-center justify-center min-h-[50vh]"><div className="animate-pulse flex flex-col items-center gap-4"><div className="w-8 h-8 border-4 border-zinc-200 border-t-zinc-900 rounded-full animate-spin"></div><span className="text-zinc-500 font-medium">Carregando ativos...</span></div></div>;

    return (
        <div className="flex-1 p-4 md:p-8 bg-zinc-50 dark:bg-zinc-950 min-h-screen">
            <header className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 mb-8">
                <div>
                    <h1 className="text-3xl font-bold text-zinc-900 dark:text-white">Meus Ativos</h1>
                    <p className="text-zinc-500 dark:text-zinc-400 mt-1">Gerencie sua estratégia e alocação</p>
                </div>
                <button 
                    className="flex items-center gap-2 px-5 py-2.5 bg-zinc-900 dark:bg-white text-white dark:text-zinc-900 font-semibold rounded-xl hover:bg-zinc-800 dark:hover:bg-zinc-100 transition-all shadow-sm" 
                    onClick={() => handleOpenModal('category', 'create')}
                >
                    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 4v16m8-8H4"></path></svg>
                    Nova Categoria
                </button>
            </header>

            {error && <div className="mb-6 p-4 bg-red-50 text-red-600 rounded-xl border border-red-100">{error}</div>}

            <div className="grid grid-cols-1 lg:grid-cols-2 xl:grid-cols-3 gap-6">
                {categories.map(category => (
                    <div key={category.id} className="bg-white dark:bg-zinc-900 rounded-2xl border border-zinc-200 dark:border-zinc-800 shadow-sm overflow-hidden flex flex-col">
                        <div className="p-5 border-b border-zinc-100 dark:border-zinc-800 flex justify-between items-center bg-zinc-50/50 dark:bg-zinc-900/50">
                            <div>
                                <h2 className="text-lg font-bold text-zinc-900 dark:text-white">{category.name}</h2>
                                <span className="inline-flex items-center mt-1 px-2.5 py-0.5 rounded-full text-xs font-medium bg-zinc-100 dark:bg-zinc-800 text-zinc-600 dark:text-zinc-400">
                                    Alvo: {category.targetPercentage}%
                                </span>
                            </div>
                            <div className="flex items-center gap-1">
                                <button className="p-2 text-zinc-400 hover:text-indigo-600 hover:bg-indigo-50 dark:hover:bg-indigo-900/20 rounded-lg transition-colors" onClick={() => handleOpenModal('category', 'edit', category)}>
                                    <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z"></path></svg>
                                </button>
                                <button className="p-2 text-zinc-400 hover:text-red-600 hover:bg-red-50 dark:hover:bg-red-900/20 rounded-lg transition-colors" onClick={() => handleDelete('category', category.id)}>
                                    <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path></svg>
                                </button>
                            </div>
                        </div>

                        <div className="p-5 flex-1 flex flex-col">
                            <div className="space-y-3 flex-1 overflow-y-auto max-h-[300px] pr-2 custom-scrollbar">
                                {category.assets && category.assets.length > 0 ? (
                                    category.assets.map(asset => (
                                        <div key={asset.id} className="group flex items-center justify-between p-3 rounded-xl hover:bg-zinc-50 dark:hover:bg-zinc-800/50 border border-transparent hover:border-zinc-100 dark:hover:border-zinc-800 transition-all">
                                            <div className="flex flex-col">
                                                <div className="flex items-center gap-2">
                                                    <span className="font-bold text-zinc-900 dark:text-white">{asset.ticker}</span>
                                                    <span className={`text-[10px] font-bold px-1.5 py-0.5 rounded ${asset.score >= 0 ? 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400' : 'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400'}`}>
                                                        {asset.score}
                                                    </span>
                                                </div>
                                                <div className="text-xs text-zinc-500 mt-1">
                                                    {asset.quantity} cotas • P.M. R$ {asset.averagePrice.toLocaleString('pt-BR')}
                                                </div>
                                            </div>
                                            <div className="flex items-center gap-3">
                                                <div className="text-right">
                                                    <div className="font-semibold text-zinc-900 dark:text-white">
                                                        R$ {asset.currentPositionValue.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}
                                                    </div>
                                                </div>
                                                <div className="flex opacity-0 group-hover:opacity-100 transition-opacity">
                                                    <button className="p-1.5 text-zinc-400 hover:text-indigo-600 rounded-md" onClick={() => handleOpenModal('asset', 'edit', asset, category.id)}>
                                                        <svg className="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z"></path></svg>
                                                    </button>
                                                    <button className="p-1.5 text-zinc-400 hover:text-red-600 rounded-md" onClick={() => handleDelete('asset', asset.id)}>
                                                        <svg className="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path></svg>
                                                    </button>
                                                </div>
                                            </div>
                                        </div>
                                    ))
                                ) : (
                                    <div className="flex flex-col items-center justify-center py-8 text-zinc-400">
                                        <svg className="w-8 h-8 mb-2 opacity-50" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="1.5" d="M20 12H4M12 20V4"></path></svg>
                                        <p className="text-sm">Nenhum ativo nesta categoria.</p>
                                    </div>
                                )}
                            </div>
                            <button 
                                className="mt-4 w-full py-2.5 border border-dashed border-zinc-300 dark:border-zinc-700 rounded-xl text-zinc-500 hover:text-zinc-900 hover:border-zinc-900 dark:hover:text-white dark:hover:border-zinc-500 hover:bg-zinc-50 dark:hover:bg-zinc-800/50 transition-all text-sm font-medium flex items-center justify-center gap-2" 
                                onClick={() => handleOpenModal('asset', 'create', null, category.id)}
                            >
                                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 4v16m8-8H4"></path></svg>
                                Adicionar Ativo
                            </button>
                        </div>
                    </div>
                ))}

                {categories.length === 0 && (
                    <div className="col-span-full flex flex-col items-center justify-center py-20 px-4 text-center border-2 border-dashed border-zinc-200 dark:border-zinc-800 rounded-3xl">
                        <div className="text-6xl mb-4 opacity-50">📁</div>
                        <h3 className="text-xl font-bold text-zinc-900 dark:text-white mb-2">Nenhuma categoria encontrada</h3>
                        <p className="text-zinc-500 max-w-md mb-6">Comece criando uma categoria para organizar seus investimentos (ex: Ações, FIIs, Renda Fixa).</p>
                        <button 
                            className="px-6 py-3 bg-zinc-900 dark:bg-white text-white dark:text-zinc-900 font-semibold rounded-xl hover:bg-zinc-800 dark:hover:bg-zinc-100 transition-all shadow-md"
                            onClick={() => handleOpenModal('category', 'create')}
                        >
                            Criar Primeira Categoria
                        </button>
                    </div>
                )}
            </div>

            {modalConfig.isOpen && (
                <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm animate-in fade-in duration-200" onClick={handleCloseModal}>
                    <div className="bg-white dark:bg-zinc-900 rounded-2xl w-full max-w-md overflow-hidden shadow-2xl border border-zinc-200 dark:border-zinc-800 animate-in zoom-in-95 duration-200" onClick={e => e.stopPropagation()}>
                        <div className="p-6 border-b border-zinc-100 dark:border-zinc-800 flex justify-between items-center">
                            <h2 className="text-xl font-bold text-zinc-900 dark:text-white">
                                {modalConfig.mode === 'create' ? 'Adicionar ' : 'Editar '}
                                {modalConfig.type === 'category' ? 'Categoria' : 'Ativo'}
                            </h2>
                            <button onClick={handleCloseModal} className="text-zinc-400 hover:text-zinc-600 dark:hover:text-zinc-300">
                                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M6 18L18 6M6 6l12 12"></path></svg>
                            </button>
                        </div>
                        
                        <form onSubmit={handleSubmit} className="p-6 space-y-4">
                            {modalConfig.type === 'category' ? (
                                <>
                                    <div className="space-y-2">
                                        <label className="text-sm font-medium text-zinc-700 dark:text-zinc-300">Nome da Categoria</label>
                                        <input 
                                            type="text" 
                                            placeholder="Ex: AÇÕES BRASILEIRAS"
                                            value={formData.name}
                                            onChange={e => setFormData({...formData, name: e.target.value.toUpperCase()})}
                                            required
                                            className="w-full px-4 py-3 rounded-xl bg-zinc-50 dark:bg-zinc-800 border border-zinc-200 dark:border-zinc-700 text-zinc-900 dark:text-white placeholder-zinc-400 focus:outline-none focus:ring-2 focus:ring-zinc-900 transition-all m-0"
                                        />
                                    </div>
                                    <div className="space-y-2">
                                        <label className="text-sm font-medium text-zinc-700 dark:text-zinc-300">Porcentagem Alvo (%)</label>
                                        <input 
                                            type="number" 
                                            placeholder="Ex: 25"
                                            value={formData.targetPercentage}
                                            onChange={e => setFormData({...formData, targetPercentage: e.target.value})}
                                            required
                                            className="w-full px-4 py-3 rounded-xl bg-zinc-50 dark:bg-zinc-800 border border-zinc-200 dark:border-zinc-700 text-zinc-900 dark:text-white placeholder-zinc-400 focus:outline-none focus:ring-2 focus:ring-zinc-900 transition-all m-0"
                                        />
                                    </div>
                                </>
                            ) : (
                                <>
                                    <div className="space-y-2">
                                        <label className="text-sm font-medium text-zinc-700 dark:text-zinc-300">Ticker (Código)</label>
                                        <input 
                                            type="text" 
                                            placeholder="Ex: PETR4"
                                            value={formData.ticker}
                                            onChange={e => setFormData({...formData, ticker: e.target.value.toUpperCase()})}
                                            required
                                            className="w-full px-4 py-3 rounded-xl bg-zinc-50 dark:bg-zinc-800 border border-zinc-200 dark:border-zinc-700 text-zinc-900 dark:text-white placeholder-zinc-400 focus:outline-none focus:ring-2 focus:ring-zinc-900 transition-all m-0"
                                        />
                                    </div>
                                    <div className="space-y-2">
                                        <label className="text-sm font-medium text-zinc-700 dark:text-zinc-300">Valor Atual da Posição (R$)</label>
                                        <input 
                                            type="number" 
                                            step="0.01"
                                            placeholder="0.00"
                                            value={formData.currentPositionValue}
                                            onChange={e => setFormData({...formData, currentPositionValue: e.target.value})}
                                            required
                                            className="w-full px-4 py-3 rounded-xl bg-zinc-50 dark:bg-zinc-800 border border-zinc-200 dark:border-zinc-700 text-zinc-900 dark:text-white placeholder-zinc-400 focus:outline-none focus:ring-2 focus:ring-zinc-900 transition-all m-0"
                                        />
                                    </div>
                                    <div className="grid grid-cols-2 gap-4">
                                        <div className="space-y-2">
                                            <label className="text-sm font-medium text-zinc-700 dark:text-zinc-300">Quantidade</label>
                                            <input 
                                                type="number" 
                                                step="0.0001"
                                                placeholder="0"
                                                value={formData.quantity}
                                                onChange={e => setFormData({...formData, quantity: e.target.value})}
                                                required
                                                className="w-full px-4 py-3 rounded-xl bg-zinc-50 dark:bg-zinc-800 border border-zinc-200 dark:border-zinc-700 text-zinc-900 dark:text-white placeholder-zinc-400 focus:outline-none focus:ring-2 focus:ring-zinc-900 transition-all m-0"
                                            />
                                        </div>
                                        <div className="space-y-2">
                                            <label className="text-sm font-medium text-zinc-700 dark:text-zinc-300">Preço Médio (R$)</label>
                                            <input 
                                                type="number" 
                                                step="0.01"
                                                placeholder="0.00"
                                                value={formData.averagePrice}
                                                onChange={e => setFormData({...formData, averagePrice: e.target.value})}
                                                required
                                                className="w-full px-4 py-3 rounded-xl bg-zinc-50 dark:bg-zinc-800 border border-zinc-200 dark:border-zinc-700 text-zinc-900 dark:text-white placeholder-zinc-400 focus:outline-none focus:ring-2 focus:ring-zinc-900 transition-all m-0"
                                            />
                                        </div>
                                    </div>
                                </>
                            )}

                            <div className="pt-4 flex gap-3">
                                <button type="button" className="flex-1 py-3 px-4 bg-zinc-100 dark:bg-zinc-800 text-zinc-700 dark:text-zinc-300 font-semibold rounded-xl hover:bg-zinc-200 dark:hover:bg-zinc-700 transition-colors m-0" onClick={handleCloseModal}>Cancelar</button>
                                <button type="submit" className="flex-1 py-3 px-4 bg-zinc-900 dark:bg-white text-white dark:text-zinc-900 font-semibold rounded-xl hover:bg-zinc-800 dark:hover:bg-zinc-100 transition-colors shadow-md m-0">Salvar</button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
};

export default Aportes;
