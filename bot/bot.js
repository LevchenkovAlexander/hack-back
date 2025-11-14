import { Bot, Keyboard } from '@maxhub/max-bot-api';

const bot = new Bot("f9LHodD0cOJ6Jy-WoRkcldBDgZ2cvifn3rvYs-UMKLvYF7v231WVWgyyUDcX3VIk99XqZKfTmw_1GyEtTpe3");

bot.command('start', (ctx) => {
  ctx.reply('Добро пожаловать! Нажмите кнопку ниже чтобы открыть Mini App:', {
  });
});


bot.start();