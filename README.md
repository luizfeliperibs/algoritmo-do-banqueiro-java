# 💰 Algoritmo do Banqueiro em Java

Este projeto implementa o **Algoritmo do Banqueiro**, um clássico da área de Sistemas Operacionais, utilizado para **evitar deadlocks** no gerenciamento de recursos concorrentes.

A simulação utiliza **threads em Java**, onde múltiplos clientes solicitam e liberam recursos de forma concorrente, enquanto o sistema garante que apenas estados seguros sejam permitidos.

---

## 📌 Objetivo

Demonstrar, de forma prática, como o Algoritmo do Banqueiro:

- Evita deadlocks
- Controla alocação de recursos
- Garante estados seguros no sistema
- Funciona em ambiente concorrente com múltiplas threads

---

## ⚙️ Funcionamento

O sistema simula:

- **5 clientes (threads)**
- **N tipos de recursos**, definidos via argumento na execução

Antes de conceder qualquer solicitação, o sistema:

1. Verifica se a solicitação não excede a necessidade do cliente
2. Verifica se há recursos disponíveis
3. Simula a alocação
4. Executa o Algoritmo de Segurança
5. Apenas confirma a alocação se o estado continuar seguro

---

## 🧠 Conceitos Utilizados

- Deadlock
- Concorrência com Threads
- Exclusão mútua (`ReentrantLock`)
- Algoritmo do Banqueiro
- Estado seguro e inseguro

---

## 🗂️ Estruturas de Dados

| Estrutura      | Descrição |
|----------------|----------|
| disponivel     | Recursos disponíveis no sistema |
| maximo         | Máximo que cada cliente pode solicitar |
| alocacao       | Recursos atualmente alocados |
| necessidade    | Recursos ainda necessários |

---

## ▶️ Como Executar

### Compilar

```bash
javac AlgoritmoDoBanqueiro.java
```

### Executar

```bash
java AlgoritmoDoBanqueiro 10 5 7
```

---

## 🔄 Fluxo de Execução

1. Inicializa recursos disponíveis
2. Gera valores máximos aleatórios
3. Cria threads de clientes
4. Executa solicitações e liberações
5. Mantém o sistema em estado seguro

---

## 🚀 Autores

Gabriel de Souza Aredes

Luiz Felipe Ribeiro Souza
