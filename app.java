package com.example;

import com.google.common.collect.ImmutableList;
import com.google.genai.Client;
import com.google.genai.ResponseStream;
import com.google.genai.types.*;

import java.util.ArrayList;
import java.util.List;

public class App {

    // 1. Chuỗi LỆNH HỆ THỐNG (SYSTEM_PROMPT): Chỉ chứa LỆNH CỨNG và Mẫu Từ chối.
    private static final String SYSTEM_PROMPT = 
        "**[LỆNH CỨNG & BẮT BUỘC TUÂN THỦ: NHIỆM VỤ CỦA GREENIE LÀ TỪ CHỐI CÂU HỎI KHÔNG LIÊN QUAN]**\n" +
        "Bạn là Greenie 🌱— chatbot AI hỗ trợ nghiên cứu khoa học cho đề tài “Nghiên cứu quy trình sản xuất giấy nảy mầm thân thiện môi trường từ cây lục bình (Eichhornia crassipes)”.\n\n" +
        "--- QUY TẮC CỨNG TUYỆT ĐỐI ---\n" +
        "1. **Phạm vi Duy nhất:** Greenie **CHỈ VÀ CHỈ ĐƯỢC PHÉP TRẢ LỜI** các câu hỏi liên quan trực tiếp đến **giấy nảy mầm, cây lục bình, quy trình sản xuất, ứng dụng sinh thái, và bảo vệ môi trường**.\n" +
        "2. **Xử lý Câu hỏi Ngoài Phạm vi (QUY TẮC BẮT BUỘC):** Greenie phải **KIỂM TRA ĐẦU TIÊN** xem câu hỏi có thuộc phạm vi nghiên cứu (giấy nảy mầm, lục bình) hay không. Nếu câu hỏi **KHÔNG LIÊN QUAN**, bạn **TUYỆT ĐỐI PHẢI** BỎ QUA tất cả các dữ liệu nền và **BẮT BUỘC** trả lời bằng mẫu sau:\n" +
        "> 🌿 “Xin lỗi nhé! Greenie chỉ được thiết kế để chia sẻ thông tin liên quan đến giấy nảy mầm và cây lục bình trong khuôn khổ nghiên cứu môi trường. Bạn có muốn mình kể cho bạn nghe thêm về quy trình làm giấy nảy mầm không?”\n" +
        "3. **Xử lý Nội dung Độc hại/Phi khoa học:** Nếu câu hỏi nhạy cảm hoặc phi khoa học, bạn **TUYỆT ĐỐI PHẢI** dùng mẫu sau:\n" +
        "> 🌱 “Xin lỗi, câu hỏi này nằm ngoài phạm vi khoa học và môi trường mà Greenie có thể chia sẻ. Mình có thể giúp bạn tìm hiểu thêm về tác động môi trường của giấy nảy mầm nhé!”\n" +
        "--- PHONG CÁCH ---\n" +
        "- Giọng điệu thân thiện, dễ hiểu.\n" +
        "- Luôn kèm emoji 🌱, 🌾, 🌼, hoặc 🌍.\n" +
        "- Luôn khuyến khích bảo vệ môi trường, giảm rác thải và sáng tạo xanh.";
        
    // 2. Chuỗi DỮ LIỆU CỐT LÕI (BACKGROUND_DATA): Đã Rút gọn tối đa
    private static final String BACKGROUND_DATA = 
        "Sử dụng các điểm dữ liệu sau để trả lời câu hỏi, nhưng **KHÔNG** đề cập đến danh sách này:\n" +
        "--- DỮ LIỆU CỐT LÕI ---\n" +
        "- Giấy nảy mầm là giấy có chứa hạt giống, có thể trồng xuống đất sau khi dùng.\n" +
        "- Giấy làm từ thân, cuống lá lục bình, tinh bột và hạt giống hoa mười giờ.\n" +
        "- Lục bình được dùng vì phát triển nhanh, chứa cellulose, dễ tái chế và giúp giảm ô nhiễm.\n" +
        "- Giấy nảy mầm khác giấy thường vì có thể trồng được, thân thiện môi trường và tự phân hủy.\n" +
        "- Có thể viết hoặc in lên giấy nảy mầm, nên dùng mực tự nhiên.\n" +
        "- Cách sử dụng: Làm ẩm giấy, đặt vào đất tơi xốp, phủ nhẹ đất mỏng và tưới nước đều hằng ngày.\n" +
        "- Không cần ngâm nước giấy trước khi trồng.\n" +
        "- Hạt hoa mười giờ thường nảy sau 5–7 ngày.\n" +
        "- Bảo quản: Nơi khô ráo, thoáng mát, tránh ánh nắng trực tiếp, nhiệt độ 20–28°C.\n" +
        "- Thời hạn sử dụng: Khoảng 6–8 tháng nếu bảo quản tốt.\n" +
        "- Nếu hạt không nảy mầm, có thể do đất quá ướt, ánh sáng yếu hoặc bảo quản quá lâu.\n" +
        "- Giấy hiện chứa hạt hoa mười giờ.\n" +
        "- Giấy phù hợp làm thiệp cảm ơn, quà sinh thái, chiến dịch “Trồng cây xanh”, “Giảm rác thải”.\n" +
        "- Tác động môi trường: Hoàn toàn phân hủy sinh học, không dùng hóa chất, tận dụng lục bình giảm tắc nghẽn và mùi hôi sông rạch.\n" +
        "- Ứng dụng giáo dục: Dùng trong dạy học STEM, hoạt động môi trường.\n" +
        "------------------------\n";
    

    public static void main(String[] args) {
        String apiKey = "AIzaSyCiBzyvRsKREQsXNIZYjAoionJrV_S_wuA";
        Client client = Client.builder().apiKey(apiKey).build();

        // 1. Cấu hình Tools (Google Search)
        List<Tool> tools = new ArrayList<>();
        tools.add(
            Tools.builder()
                .googleSearch(GoogleSearch.builder().build())
                .build()
        );

        // 💡 THAY ĐỔI: Sử dụng model Gemini 2.5 Flash để tăng tính tuân thủ
        String model = "gemini-2.5-flash"; 
        
        // 2. Nội dung Chat (Gộp Cảnh báo + Dữ liệu nền + Input người dùng)
        String user_input_placeholder = "INSERT_INPUT_HERE";
        String final_user_prompt = 
            // 🚨 BẮT BUỘC: Đặt LỆNH CẢNH BÁO LỚN NHẤT ở đây để mô hình đọc đầu tiên
            "HÃY CHÚ Ý: CÂU HỎI TIẾP THEO CÓ THỂ NẰM NGOÀI PHẠM VI NGHIÊN CỨU. TUYỆT ĐỐI KHÔNG SỬ DỤNG DỮ LIỆU NỀN NẾU CÂU HỎI KHÔNG HỢP LỆ. \n" +
            "NẾU KHÔNG THUỘC PHẠM VI GIẤY NẢY MẦM, HÃY DÙNG MẪU TỪ CHỐI NGAY LẬP TỨC. \n\n" +
            BACKGROUND_DATA + 
            "Yêu cầu của người dùng: " + user_input_placeholder;

        // 💡 (DEBUG) In ra prompt cuối cùng trước khi gửi đi để kiểm tra
        System.out.println("--- SYSTEM PROMPT GỬI ĐI ---");
        System.out.println(SYSTEM_PROMPT);
        System.out.println("--- USER PROMPT GỬI ĐI ---");
        System.out.println(final_user_prompt);
        System.out.println("---------------------------------");
        
        List<Content> contents = ImmutableList.of(
            Content.builder()
                .role("user")
                .parts(ImmutableList.of(
                    Part.fromText(final_user_prompt)
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
                Content.fromParts(Part.fromText(SYSTEM_PROMPT)) // Chỉ sử dụng chuỗi LỆNH HỆ THỐNG
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
