package com.example;

import com.google.genai.Client;
import com.google.genai.Response;
import com.google.genai.types.Content;
import com.google.genai.types.Part;
import com.google.genai.types.GenerateContentRequest;
import com.google.genai.types.GenerateContentResponse;

import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

public class ChatbotGreenie {

    private static final String API_KEY = "AIzaSyCiBzyvRsKREQsXNIZYjAoionJrV_S_wuA";
    private static final String MODEL = "gemini-2.0-pro-exp-02-05";
    private final Client client;

    public ChatbotGreenie() {
        this.client = new Client(API_KEY);
    }

    public String sendMessage(String userMessage) {
        try {
            // Chuẩn bị nội dung yêu cầu
            Content userContent = new Content("user", List.of(new Part(userMessage)));
            Content systemInstruction = new Content("system", List.of(
                    new Part("Bạn là Greenie 🌱 — chatbot hỗ trợ nghiên cứu khoa học về giấy nảy mầm từ cây lục bình. "
                            + "Hãy trả lời thân thiện, rõ ràng, không dùng dấu *.")
            ));

            GenerateContentRequest request = new GenerateContentRequest.Builder()
                    .setModel(MODEL)
                    .setContents(List.of(userContent))
                    .setSystemInstruction(systemInstruction)
                    .build();

            // Gửi yêu cầu đến API Gemini
            GenerateContentResponse response = client.models().generateContent(request);

            // Trích xuất phản hồi
            if (response.getCandidates() != null && !response.getCandidates().isEmpty()) {
                return response.getCandidates().get(0).getContent().getParts().get(0).getText();
            } else {
                return "⚠️ Không có phản hồi từ chatbot.";
            }

        } catch (Exception e) {
            System.err.println("❌ Lỗi API: " + e.getMessage());
            return "❌ Lỗi: " + e.getMessage();
        }
    }

    public static void main(String[] args) {
        ChatbotGreenie bot = new ChatbotGreenie();
        Scanner scanner = new Scanner(System.in);

        System.out.println("🌿 Greenie sẵn sàng! Hỏi tôi về nghiên cứu giấy nảy mầm từ cây lục bình nhé.");
        System.out.println("Nhập 'exit' để thoát.\n");

        while (true) {
            System.out.print("👤 Bạn: ");
            String userInput = scanner.nextLine();

            if (userInput.equalsIgnoreCase("exit")) break;

            String reply = bot.sendMessage(userInput);
            System.out.println("🤖 Greenie: " + reply + "\n");
        }

        scanner.close();
    }
}
