package com.brunnakampferd.temvorax.ui.screens.legal

/**
 * Textos de Termos de Uso e Política de Privacidade do Temvorax.
 *
 * ⚠️ IMPORTANTE: isso é um RASCUNHO GENÉRICO pra você ter algo funcional
 * enquanto o app está em desenvolvimento. NÃO é aconselhamento jurídico.
 * Antes de publicar o app de verdade (mesmo em beta fechado com poucos
 * usuários), vale a pena mostrar esse texto pra um advogado ou usar um
 * gerador de políticas específico pra LGPD, porque:
 *  - o Temvorax coleta dados pessoais (e-mail, nome, dados de uso)
 *  - usa autenticação de terceiros (Google)
 *  - guarda dados em nuvem (Firebase/Firestore)
 * Esses pontos têm implicações legais específicas que eu não posso garantir
 * que estão 100% cobertas aqui.
 *
 * Deixei os textos como constantes aqui pra ser fácil de editar depois —
 * você só troca o conteúdo da string, não precisa mexer na tela.
 */

const val TERMOS_DE_USO = """
Última atualização: [preencher data]

1. Aceitação dos Termos
Ao criar uma conta ou utilizar o Temvorax, você concorda com estes Termos de Uso. Se não concordar com algum ponto, pedimos que não utilize o aplicativo.

2. Descrição do Serviço
O Temvorax é um aplicativo de organização pessoal, profissional e acadêmica, que utiliza elementos de gamificação para ajudar o usuário a acompanhar tarefas, hábitos e metas.

3. Cadastro e Conta
Para usar o Temvorax, você precisa criar uma conta com e-mail e senha ou por meio de login social (Google). Você é responsável por manter a confidencialidade da sua senha e por todas as atividades realizadas na sua conta.

4. Uso Adequado
Você concorda em não utilizar o Temvorax para fins ilegais, ofensivos ou que violem direitos de terceiros. Nos reservamos o direito de suspender contas que violem estes termos.

5. Conteúdo do Usuário
As informações que você insere no aplicativo (tarefas, notas, metas, etc.) pertencem a você. O Temvorax armazena esses dados para fornecer o serviço, mas não reivindica propriedade sobre o conteúdo.

6. Gamificação e Recompensas
Pontos de experiência (XP), níveis, medalhas e itens da loja de recompensas não possuem valor monetário real e existem apenas para fins de engajamento dentro do aplicativo.

7. Disponibilidade do Serviço
Fazemos o possível para manter o app disponível, mas não garantimos funcionamento ininterrupto. Manutenções, atualizações ou problemas técnicos podem causar indisponibilidade temporária.

8. Alterações nos Termos
Podemos atualizar estes Termos de Uso periodicamente. Mudanças significativas serão comunicadas dentro do aplicativo.

9. Contato
Dúvidas sobre estes termos podem ser enviadas para: [preencher e-mail de contato]
"""

const val POLITICA_DE_PRIVACIDADE = """
Última atualização: [preencher data]

1. Quais Dados Coletamos
Coletamos as seguintes informações quando você usa o Temvorax:
- Dados de cadastro: nome, e-mail, foto de perfil (se você usar login com Google)
- Dados de uso: tarefas, hábitos, metas, projeções e demais conteúdos criados por você
- Dados técnicos: informações básicas do dispositivo, para fins de suporte e melhoria do app

2. Como Usamos Seus Dados
Usamos seus dados para:
- Fornecer e manter as funcionalidades do aplicativo
- Sincronizar seu conteúdo entre dispositivos
- Melhorar a experiência do usuário
- Enviar notificações relacionadas às suas tarefas e conquistas (você pode desativar isso nas configurações)

3. Armazenamento e Segurança
Seus dados são armazenados de forma segura utilizando os serviços do Firebase (Google). Utilizamos autenticação criptografada e seguimos boas práticas de segurança para proteger suas informações.

4. Compartilhamento de Dados
Não vendemos nem compartilhamos seus dados pessoais com terceiros para fins de marketing. Seus dados só são compartilhados com provedores de infraestrutura essenciais ao funcionamento do app (como o Firebase), conforme necessário para operar o serviço.

5. Seus Direitos (LGPD)
De acordo com a Lei Geral de Proteção de Dados (LGPD), você tem direito a:
- Acessar os dados que temos sobre você
- Corrigir dados incorretos
- Solicitar a exclusão da sua conta e dados associados
- Revogar consentimentos dados anteriormente
Para exercer esses direitos, entre em contato pelo e-mail: [preencher e-mail de contato]

6. Retenção de Dados
Mantemos seus dados enquanto sua conta estiver ativa. Caso você solicite a exclusão da conta, seus dados serão removidos dos nossos sistemas em até [preencher prazo, ex: 30 dias], exceto quando a retenção for exigida por lei.

7. Alterações nesta Política
Podemos atualizar esta Política de Privacidade periodicamente. Mudanças significativas serão comunicadas dentro do aplicativo.

8. Contato
Dúvidas sobre esta política podem ser enviadas para: [preencher e-mail de contato]
"""