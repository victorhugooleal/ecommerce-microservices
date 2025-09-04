# 📫 Coleção Postman - E-commerce Monolith API

## 🚀 Como Importar e Usar

### 1. **Importar no Postman**
1. Abra o Postman
2. Clique em **Import** (canto superior esquerdo)
3. Arraste ou selecione os arquivos:
   - `E-commerce_Monolith_API.postman_collection.json` (Coleção)
   - `E-commerce_Local.postman_environment.json` (Environment)

### 2. **Configurar o Environment**
1. No canto superior direito, selecione o environment **"E-commerce Local"**
2. Verifique se a URL base está configurada como `http://localhost:8080`

### 3. **Testar a Aplicação**

#### **Passo 1: Verificar se a aplicação está rodando**
- Execute: `Health Check > Application Health`
- Deve retornar: `{"status":"UP"}`

#### **Passo 2: Fazer Login**
- **Como Admin**: Execute `Authentication > Login Admin`
- **Como User**: Execute `Authentication > Login User`
- ✅ O token JWT será automaticamente salvo e usado nas próximas requisições

#### **Passo 3: Testar as APIs**

**📱 Produtos (Público)**
- `Products > Get All Products` - Listar todos os produtos
- `Products > Get Product by ID` - Ver detalhes de um produto
- `Products > Search Products` - Buscar produtos por nome

**👥 Usuários (Requer Login)**
- `Users > Get User by ID` - Ver perfil do usuário
- `Users > Update User` - Atualizar dados do usuário
- `Users > Get All Users` - Listar todos (apenas Admin)

**🛒 Pedidos (Requer Login)**
- `Orders > Create Order` - Criar novo pedido
- `Orders > Get My Orders` - Ver meus pedidos
- `Orders > Get Order by ID` - Ver detalhes de um pedido

**🔧 Gestão de Pedidos (Apenas Admin)**
- `Orders > Confirm Order` - Confirmar pedido
- `Orders > Start Processing Order` - Iniciar processamento
- `Orders > Ship Order` - Enviar pedido
- `Orders > Deliver Order` - Entregar pedido
- `Orders > Cancel Order` - Cancelar pedido

## 🔐 Credenciais de Teste

| Usuário | Email | Senha | Perfil |
|---------|-------|-------|--------|
| Admin | `admin@ecommerce.com` | `admin123` | Administrador |
| User | `joao@email.com` | `user123` | Usuário comum |

## 📊 Fluxo de Teste Completo

### **Cenário 1: Usuário Comum**
```
1. Login User
2. Get All Products
3. Create Order (com produtos existentes)
4. Get My Orders
5. Get Order by ID
```

### **Cenário 2: Administrador**
```
1. Login Admin
2. Get All Users
3. Create Product
4. Get All Orders
5. Confirm Order → Process → Ship → Deliver
```

### **Cenário 3: Gestão de Estoque**
```
1. Login Admin
2. Get Product by ID (verificar stock)
3. Create Order (reduz estoque)
4. Update Product (ajustar estoque)
```

## 🛠️ Recursos Automáticos

- ✅ **Token JWT**: Salvo automaticamente após login
- ✅ **Order ID**: Salvo automaticamente após criar pedido
- ✅ **Environment Variables**: Configuração centralizada
- ✅ **Error Handling**: Respostas de erro são exibidas claramente

## 🔍 Dicas de Uso

1. **Sempre faça login primeiro** antes de testar endpoints protegidos
2. **Use o Admin** para testar funcionalidades administrativas
3. **Verifique o Console** do Postman para logs automáticos
4. **IDs dos produtos de teste**: 1 a 10 (criados automaticamente)
5. **Para testar fluxo completo**: Crie um pedido e execute todas as etapas de processamento

## 🚨 Solução de Problemas

**❌ Erro 401 Unauthorized**
- Solução: Execute o login novamente

**❌ Erro 403 Forbidden**  
- Solução: Use credenciais de Admin para endpoints administrativos

**❌ Erro 404 Not Found**
- Solução: Verifique se o ID do recurso existe

**❌ Connection Error**
- Solução: Verifique se a aplicação está rodando (`docker-compose up`)

## 📦 Estrutura da Coleção

```
📁 E-commerce Monolith API
├── 🔐 Authentication
│   ├── Login Admin
│   ├── Login User
│   └── Register New User
├── 👥 Users
│   ├── Get All Users (Admin)
│   ├── Get User by ID
│   ├── Update User
│   └── Delete User (Admin)
├── 📱 Products
│   ├── Get All Products
│   ├── Get Product by ID
│   ├── Search Products
│   ├── Create Product (Admin)
│   ├── Update Product (Admin)
│   └── Delete Product (Admin)
├── 🛒 Orders
│   ├── Get All Orders (Admin)
│   ├── Get My Orders
│   ├── Get Order by ID
│   ├── Create Order
│   ├── Confirm Order (Admin)
│   ├── Start Processing (Admin)
│   ├── Ship Order (Admin)
│   ├── Deliver Order (Admin)
│   └── Cancel Order
└── ❤️ Health Check
    └── Application Health
```

---

**🎯 Pronto para testar!** A coleção está configurada para cobrir todos os cenários de uso da aplicação.
