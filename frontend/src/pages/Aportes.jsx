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

    if (loading) return <div className="aportes-container"><div className="loading">Carregando...</div></div>;

    return (
        <div className="aportes-container">
            <header className="header-section">
                <div>
                    <h1>Meus Ativos</h1>
                    <p>Gerencie sua estratégia e alocação</p>
                </div>
                <button className="add-category-btn" onClick={() => handleOpenModal('category', 'create')}>
                    + Nova Categoria
                </button>
            </header>

            {error && <div className="error-msg">{error}</div>}

            <div className="categories-grid">
                {categories.map(category => (
                    <div key={category.id} className="category-card">
                        <div className="category-header">
                            <div className="category-info">
                                <h2>{category.name}</h2>
                                <span className="target-badge">Alvo: {category.targetPercentage}%</span>
                            </div>
                            <div className="category-actions">
                                <button className="icon-btn" onClick={() => handleOpenModal('category', 'edit', category)}>
                                    ✏️
                                </button>
                                <button className="icon-btn delete" onClick={() => handleDelete('category', category.id)}>
                                    🗑️
                                </button>
                            </div>
                        </div>

                        <div className="assets-list">
                            {category.assets && category.assets.length > 0 ? (
                                category.assets.map(asset => (
                                    <div key={asset.id} className="asset-item">
                                        <div className="asset-main">
                                            <span className="asset-ticker">{asset.ticker}</span>
                                            <span className={`asset-score ${asset.score >= 0 ? 'score-positive' : 'score-negative'}`}>
                                                Score: {asset.score}
                                            </span>
                                        </div>
                                        <div className="asset-values">
                                            <div className="asset-price">R$ {asset.currentPositionValue.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}</div>
                                            <div className="asset-qty">{asset.quantity} cotas</div>
                                        </div>
                                        <div className="asset-actions">
                                            <button className="icon-btn" onClick={() => handleOpenModal('asset', 'edit', asset, category.id)}>
                                                ✏️
                                            </button>
                                            <button className="icon-btn delete" onClick={() => handleDelete('asset', asset.id)}>
                                                🗑️
                                            </button>
                                        </div>
                                    </div>
                                ))
                            ) : (
                                <p style={{ fontSize: '14px', color: 'var(--text-placeholder)', textAlign: 'center', padding: '20px' }}>
                                    Nenhum ativo nesta categoria.
                                </p>
                            )}
                            <button className="add-asset-btn" onClick={() => handleOpenModal('asset', 'create', null, category.id)}>
                                + Adicionar Ativo
                            </button>
                        </div>
                    </div>
                ))}

                {categories.length === 0 && (
                    <div className="empty-state">
                        <div style={{ fontSize: '48px' }}>📁</div>
                        <h3>Nenhuma categoria encontrada</h3>
                        <p>Comece criando uma categoria para seus investimentos (ex: Ações, FIIs).</p>
                        <button onClick={() => handleOpenModal('category', 'create')}>Criar Primeira Categoria</button>
                    </div>
                )}
            </div>

            {modalConfig.isOpen && (
                <div className="modal-overlay" onClick={handleCloseModal}>
                    <div className="modal-content" onClick={e => e.stopPropagation()}>
                        <h2>
                            {modalConfig.mode === 'create' ? 'Adicionar ' : 'Editar '}
                            {modalConfig.type === 'category' ? 'Categoria' : 'Ativo'}
                        </h2>
                        
                        <form onSubmit={handleSubmit}>
                            {modalConfig.type === 'category' ? (
                                <>
                                    <div className="form-group">
                                        <label>Nome da Categoria</label>
                                        <input 
                                            type="text" 
                                            placeholder="Ex: Ações Brasileiras"
                                            value={formData.name}
                                            onChange={e => setFormData({...formData, name: e.target.value})}
                                            required
                                        />
                                    </div>
                                    <div className="form-group">
                                        <label>Porcentagem Alvo (%)</label>
                                        <input 
                                            type="number" 
                                            placeholder="Ex: 25"
                                            value={formData.targetPercentage}
                                            onChange={e => setFormData({...formData, targetPercentage: e.target.value})}
                                            required
                                        />
                                    </div>
                                </>
                            ) : (
                                <>
                                    <div className="form-group">
                                        <label>Ticker (Código)</label>
                                        <input 
                                            type="text" 
                                            placeholder="Ex: PETR4"
                                            value={formData.ticker}
                                            onChange={e => setFormData({...formData, ticker: e.target.value.toUpperCase()})}
                                            required
                                        />
                                    </div>
                                    <div className="form-group">
                                        <label>Valor Atual da Posição (R$)</label>
                                        <input 
                                            type="number" 
                                            step="0.01"
                                            placeholder="0.00"
                                            value={formData.currentPositionValue}
                                            onChange={e => setFormData({...formData, currentPositionValue: e.target.value})}
                                            required
                                        />
                                    </div>
                                    <div className="form-group">
                                        <label>Quantidade</label>
                                        <input 
                                            type="number" 
                                            step="0.0001"
                                            placeholder="0"
                                            value={formData.quantity}
                                            onChange={e => setFormData({...formData, quantity: e.target.value})}
                                            required
                                        />
                                    </div>
                                    <div className="form-group">
                                        <label>Preço Médio (R$)</label>
                                        <input 
                                            type="number" 
                                            step="0.01"
                                            placeholder="0.00"
                                            value={formData.averagePrice}
                                            onChange={e => setFormData({...formData, averagePrice: e.target.value})}
                                            required
                                        />
                                    </div>
                                </>
                            )}

                            <div className="modal-footer">
                                <button type="button" className="cancel-btn" onClick={handleCloseModal}>Cancelar</button>
                                <button type="submit">Salvar</button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
};

export default Aportes;
