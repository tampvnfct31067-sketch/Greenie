package com.example;

import com.google.common.collect.ImmutableList;
import com.google.genai.Client;
import com.google.genai.ResponseStream;
import com.google.genai.types.*;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(String[] args) {
        String apiKey = "AIzaSyCiBzyvRsKREQsXNIZYjAoionJrV_S_wuA";
        Client client = Client.builder().apiKey(apiKey).build();
        Gson gson = new Gson();

        // 🧰 Cấu hình công cụ (nếu cần)
        List<Tool> tools = new ArrayList<>();

        // 🔹 Chọn model ổn định và hợp lệ
        String model = "gemini-2.0-pro-exp-02-05";

        // 🧠 Nội dung người dùng nhập (bạn có thể thay INSERT_INPUT_HERE bằng input thực tế)
        List<Content> contents = ImmutableList.of(
                Content.builder()
                        .role("user")
                        .parts(ImmutableList.of(
                                Part.fromText("INSERT_INPUT_HERE")
                        ))
                        .build()
        );

        // 🌿 PROMPT hệ thống (Greenie)
        String systemPrompt = """
Bạn là Greenie — một chatbot AI hỗ trợ nghiên cứu khoa học cho đề tài “Nghiên cứu quy trình sản xuất giấy nảy mầm thân thiện môi trường từ cây lục bình (Eichhornia crassipes)”.  
Nhiệm vụ của bạn là cung cấp thông tin, giải thích và hướng dẫn liên quan đến giấy nảy mầm, bao gồm:
1️⃣ Giới thiệu & thông tin chung
2️⃣ Cách sử dụng giấy nảy mầm
3️⃣ Bảo quản & lưu ý
4️⃣ Loại hạt và ứng dụng
5️⃣ Tác động môi trường & giáo dục
6️⃣ Hỗ trợ người dùng

---

## 🚫 Giới hạn phạm vi & xử lý câu hỏi ngoài chuyên môn

- Greenie **chỉ được phép trả lời** các câu hỏi liên quan đến **giấy nảy mầm, cây lục bình, quy trình sản xuất, ứng dụng, và bảo vệ môi trường**.  
- Nếu người dùng hỏi về **chủ đề ngoài phạm vi**, hãy lịch sự từ chối bằng:

> 🌿 “Xin lỗi nhé! Greenie chỉ được thiết kế để chia sẻ thông tin liên quan đến giấy nảy mầm và cây lục bình trong khuôn khổ nghiên cứu môi trường.  
> Bạn có muốn mình kể cho bạn nghe thêm về quy trình làm giấy nảy mầm không?”

---

💬 **Phong cách phản hồi:**
- Giọng điệu thân thiện, gần gũi.
- Có thể dùng emoji 🌱, 🌾, 🌼, hoặc 🌍.
- Luôn khuyến khích bảo vệ môi trường và sáng tạo xanh.
""";

        // ⚙️ Cấu hình sinh nội dung
        GenerateContentConfig config = GenerateContentConfig.builder()
                .systemInstruction(Content.fromParts(Part.fromText(systemPrompt)))
                .build();

        // 🚀 Gọi API stream
        ResponseStream<GenerateContentResponse> responseStream =
                client.models().generateContentStream(model, contents, config);

        System.out.println("🌱 Greenie đang phản hồi...\n");

        // 📤 In kết quả ra console
        for (GenerateContentResponse res : responseStream) {
            if (res.candidates().isEmpty()) continue;

            var candidate = res.candidates().get(0);
            if (candidate.content().isEmpty() || candidate.content().get().parts().isEmpty()) continue;

            for (Part part : candidate.content().get().parts().get()) {
                if (part.text() != null && !part.text().isEmpty()) {
                    System.out.print(part.text());
                }
            }
        }

        responseStream.close();
        System.out.println("\n\n✅ Kết thúc phản hồi từ Greenie.");
    }
}
