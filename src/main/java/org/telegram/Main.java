package org.telegram;

/**
 * Created by Gomgom on 2016-04-19.
 * 봇을 소환하는 메인 클래스입니다.
 */

import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.updateshandlers.TellMiseHandlers;

public class Main {
    public static void main(String[] args) {

        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(new TellMiseHandlers()); // 미세먼지 봇을 등록합니다.
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
