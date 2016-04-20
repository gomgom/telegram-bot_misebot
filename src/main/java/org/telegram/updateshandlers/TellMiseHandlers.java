package org.telegram.updateshandlers;

/**
 * Created by Gomgom on 2016-04-19.
 * TellMiseHandlers 클래스는 메시지를 전송받고 답장하는 역할을 합니다.
 */

import org.telegram.BotConfig;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.AnswerReport;

public class TellMiseHandlers extends TelegramLongPollingBot {

    @Override
    public String getBotUsername() {
        return BotConfig.USERNAMETELLMISE;
    }

    @Override
    public void onUpdateReceived(Update update) {

        // 업데이트가 메시지를 가지고 있는지 확인합니다.
        if(update.hasMessage()){
            Message message = update.getMessage();
            String answerMessage = message.getText().toString(); // 메시지를 String 형태로 받아옵니다.
            char answerLocation[] = new char[10];
            
            if(answerMessage.startsWith("/ㅁㅅ ")) { 
            	answerMessage.getChars(4, answerMessage.length(), answerLocation, 0);
            // 지역명이 전달되었다면 char 배열로 받아옵니다. (최대 10자로 제한두고자 함)
            	
                SendMessage sendMessageRequest = new SendMessage();
                sendMessageRequest.setChatId(message.getChatId().toString()); //who should get the message? the sender from which we got the message...
                String str = new String(answerLocation, 0, answerMessage.length()-4); // 지역명을 String으로 재반환합니다.
                sendMessageRequest.setText(AnswerReport.getPMStatus(str)); // 지역명을 AnswerReport 클래스로 넘겨줍니다.
                try {
                    sendMessage(sendMessageRequest); //at the end, so some magic and send the message ;)
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }//end catch()
                
            }//end if()
        }//end  if()
    }//end onUpdateReceived()

    @Override
    public String getBotToken() {
        return BotConfig.TOKENTELLMISE;
    }

}