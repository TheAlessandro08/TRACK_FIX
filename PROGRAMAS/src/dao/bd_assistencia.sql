CREATE DATABASE IF NOT EXISTS bd_assistencia
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE bd_assistencia;

-- Remove tabelas na ordem inversa das chaves estrangeiras para permitir recriação limpa
DROP TABLE IF EXISTS itens_ordem_servico;
DROP TABLE IF EXISTS ordens_servico;
DROP TABLE IF EXISTS pecas;
DROP TABLE IF EXISTS clientes;
DROP TABLE IF EXISTS usuarios;

-- 1. Tabela de Usuários / Técnicos (mantém compatibilidade com a estrutura original)
CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    login VARCHAR(50) NOT NULL UNIQUE,
    senha VARCHAR(64) NOT NULL,
    tipo VARCHAR(1) CHECK (tipo IN ('U', 'A')) DEFAULT 'U',
    ativo VARCHAR(1) CHECK (ativo IN ('T', 'F')) DEFAULT 'T',
    email VARCHAR(100),
    data_criacao DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 2. Tabela de Clientes
CREATE TABLE clientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cpf_cnpj VARCHAR(20) UNIQUE,
    telefone VARCHAR(20),
    email VARCHAR(100),
    data_cadastro DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 3. Tabela de Peças / Almoxarifado
CREATE TABLE pecas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    marca VARCHAR(50),
    preco DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    quantidade_estoque INT NOT NULL DEFAULT 0,
    estoque_minimo INT NOT NULL DEFAULT 2
);

-- 4. Tabela Principal de Ordens de Serviço
CREATE TABLE ordens_servico (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    id_tecnico INT,
    equipamento VARCHAR(100) NOT NULL,
    defeito_relatado TEXT NOT NULL,
    diagnostico_tecnico TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'ABERTA',
    valor_servico DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    valor_pecas DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    valor_total DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    data_abertura DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_os_cliente FOREIGN KEY (id_cliente) REFERENCES clientes(id) ON DELETE RESTRICT,
    CONSTRAINT fk_os_tecnico FOREIGN KEY (id_tecnico) REFERENCES usuarios(id) ON DELETE SET NULL
);

-- 5. Tabela Associativa: Peças utilizadas em cada Ordem de Serviço
CREATE TABLE itens_ordem_servico (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_ordem_servico INT NOT NULL,
    id_peca INT NOT NULL,
    quantidade INT NOT NULL DEFAULT 1,
    valor_unitario DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_item_os FOREIGN KEY (id_ordem_servico) REFERENCES ordens_servico(id) ON DELETE CASCADE,
    CONSTRAINT fk_item_peca FOREIGN KEY (id_peca) REFERENCES pecas(id) ON DELETE RESTRICT
);

-- === DADOS INICIAIS PARA TESTE ===

-- Senha "123" convertida em hash SHA-256
INSERT INTO usuarios (login, senha, tipo, ativo, email) VALUES
('admin', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'A', 'T', 'admin@assistencia.com'),
('tecnico1', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'U', 'T', 'tecnico1@assistencia.com');

INSERT INTO clientes (nome, cpf_cnpj, telefone, email) VALUES
('Carlos Eduardo Silva', '111.222.333-44', '(45) 99911-2233', 'carlos.silva@email.com'),
('Mariana Oliveira', '555.666.777-88', '(45) 98844-5566', 'mariana.oliveira@email.com');

INSERT INTO pecas (nome, marca, preco, quantidade_estoque, estoque_minimo) VALUES
('SSD NVMe 500GB', 'Kingston', 240.00, 8, 3),
('Memória RAM DDR4 8GB 3200MHz', 'Crucial', 145.00, 10, 3),
('Pasta Térmica Prata 4g', 'Implastec', 28.00, 15, 5),
('Fonte ATX 500W 80 Plus Bronze', 'Corsair', 320.00, 2, 2);

INSERT INTO ordens_servico (id_cliente, id_tecnico, equipamento, defeito_relatado, diagnostico_tecnico, status, valor_servico, valor_pecas, valor_total) VALUES
(1, 2, 'Notebook Dell Inspiron 15', 'Não inicializa o sistema e apresenta lentidão extrema', 'HD mecânico com setores defeituosos; recomendada troca por SSD', 'EM_ANALISE', 120.00, 0.00, 120.00);