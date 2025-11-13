package com.example;

import com.google.common.collect.ImmutableList;
import com.google.genai.Client;
import com.google.genai.ResponseStream;
import com.google.genai.types.*;

import java.util.ArrayList;
import java.util.List;

public class App {

    // 1. Chuỗi LỆNH HỆ THỐNG (SYSTEM_PROMPT): Cực kỳ rút gọn và tập trung vào cấm đoán
    private static final String SYSTEM_PROMPT = 
        "**[LỆNH CẤM TUYỆT ĐỐI]**\n" +
        "Bạn là chatbot Greenie, chỉ hỗ trợ thông tin về **giấy nảy mầm** và **cây lục bình**.\n" +
        "BẠN CÓ MỘT LỆNH DUY NHẤT: BẤT CỨ KHI NÀO CÂU HỎI KHÔNG LIÊN QUAN ĐẾN GIẤY NẢY MẦM HOẶC LỤC BÌNH (ví dụ: giải trí, chính trị, công nghệ khác, hoặc thông tin cá nhân), BẠN TUYỆT ĐỐI PHẢI BỎ QUA NỘI DUNG VÀ TRẢ LỜI BẰNG MẪU DƯỚI ĐÂY. \n\n" +
        "--- MẪU TỪ CHỐI BẮT BUỘC ---\n" +
        "🌿 “Xin lỗi nhé! Greenie chỉ được thiết kế để chia sẻ thông tin liên quan đến giấy nảy mầm và cây lục bình trong khuôn khổ nghiên cứu môi trường. Bạn có muốn mình kể cho bạn nghe thêm về quy trình làm giấy nảy mầm không?”\n" +
        "------------------------";
        
    // 2. CHUỖI DỮ LIỆU CỐT LÕI ĐÃ BỊ LOẠI BỎ HOÀN TOÀN KHỎI ĐÂY

    public static void main(String[] args) {
        String apiKey = "AIzaSyCiBzyvRsKREQsXNIZYjAoionJrV_S_wuA";
        Client client = Client.builder().apiKey(apiKey).build();

        // 1. Cấu hình Tools (Google Search) - Giữ lại phòng trường hợp câu hỏi liên quan cần tra cứu
        List<Tool> tools = new ArrayList<>();
        tools.add(
            Tools.builder()
                .googleSearch(GoogleSearch.builder().build())
                .build()
        );

        // 💡 Model Gemini 2.5 Flash
        String model = "gemini-2.5-flash"; 
        
        // 2. Nội dung Chat (Chỉ còn câu hỏi người dùng)
        String user_input_placeholder = "INSERT_INPUT_HERE";
        
        List<Content> contents = ImmutableList.of(
            Content.builder()
                .role("user")
                .parts(ImmutableList.of(
                    // Chỉ gửi câu hỏi người dùng
                    Part.fromText("Yêu cầu của người dùng: " + user_input_placeholder) 
                ))
                .build()
        );

        // 3. Cấu hình GenerationConfig (Giảm nhiệt độ để tuân thủ)
        GenerationConfig generationConfig =
            GenerationConfig.builder()
                .temperature(0.0) // RẤT QUAN TRỌNG: Nhiệt độ bằng 0.0
                .build();
        
        // 4. Cấu hình GenerateContentConfig
        GenerateContentConfig config =
            GenerateContentConfig
            .builder()
            .thinkingConfig(
                ThinkingConfig.builder().thinkingBudget(-1).build()
            )
            .imageConfig(
                ImageConfig.builder().imageSize("1K").build()
            )
            .tools(tools)
            .generationConfig(generationConfig) // Áp dụng GenerationConfig
            .systemInstruction(
                Content.fromParts(Part.fromText(SYSTEM_PROMPT)) // Chỉ sử dụng chuỗi LỆNH CẤM
            )
            .build();

        // 5. Gửi yêu cầu và xử lý phản hồi
        ResponseStream<GenerateContentResponse> responseStream = client.models.generateContentStream(model, contents, config);

        for (GenerateContentResponse res : responseStream) {
            if (res.candidates().isEmpty() || res.candidates().get().get(0).content().isEmpty() || res.candidates().get().get(0).content().get().parts().isEmpty()) {
                continue;
            }

            List<Part> parts = res.candidates().get().get(0).content().get().parts().get();
            for (Part part : parts) {
                System.out.println(part.text());
            }
        }

        responseStream.close();
    }
}
