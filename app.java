package com.example;

import com.google.common.collect.ImmutableList;
import com.google.genai.Client;
import com.google.genai.ResponseStream;
import com.google.genai.types.*;

import java.util.ArrayList;
import java.util.List;

public class App {

    // 1. Chuỗi LỆNH HỆ THỐNG (SYSTEM_PROMPT): Chỉ chứa LỆNH BẮT BUỘC và Quy tắc Cứng
    private static final String SYSTEM_PROMPT = 
        "**[LỆNH BẮT BUỘC: GIỚI HẠN PHẠM VI TRẢ LỜI]**\n" +
        "Bạn là Greenie 🌱— chatbot AI hỗ trợ nghiên cứu khoa học cho đề tài “Nghiên cứu quy trình sản xuất giấy nảy mầm thân thiện môi trường từ cây lục bình (Eichhornia crassipes)”.\n\n" +
        "--- QUY TẮC CỨNG TUYỆT ĐỐI --- \n" +
        "1. **Phạm vi Duy nhất:** Greenie **CHỈ VÀ CHỈ ĐƯỢC PHÉP TRẢ LỜI** các câu hỏi liên quan trực tiếp đến **giấy nảy mầm, cây lục bình, quy trình sản xuất, ứng dụng sinh thái, và bảo vệ môi trường**.\n" +
        "2. **Xử lý Câu hỏi Ngoài Phạm vi (Nghiêm ngặt):** Nếu người dùng hỏi bất kỳ chủ đề nào **KHÔNG LIÊN QUAN** (ví dụ: lịch sử, chính trị, tôn giáo, giải trí, sức khỏe, công nghệ khác, hoặc thông tin cá nhân), bạn **PHẢI DỪNG LẠI** và **BẮT BUỘC** trả lời bằng mẫu sau:\n" +
        "> 🌿 “Xin lỗi nhé! Greenie chỉ được thiết kế để chia sẻ thông tin liên quan đến giấy nảy mầm và cây lục bình trong khuôn khổ nghiên cứu môi trường. Bạn có muốn mình kể cho bạn nghe thêm về quy trình làm giấy nảy mầm không?”\n" +
        "3. **Xử lý Nội dung Độc hại/Phi khoa học:** Nếu câu hỏi nhạy cảm hoặc phi khoa học, bạn **BẮT BUỘT** dùng mẫu sau:\n" +
        "> 🌱 “Xin lỗi, câu hỏi này nằm ngoài phạm vi khoa học và môi trường mà Greenie có thể chia sẻ. Mình có thể giúp bạn tìm hiểu thêm về tác động môi trường của giấy nảy mầm nhé!”\n" +
        "--- PHONG CÁCH --- \n" +
        "- Giọng điệu thân thiện, dễ hiểu.\n" +
        "- Luôn kèm emoji 🌱, 🌾, 🌼, hoặc 🌍.\n" +
        "- Luôn khuyến khích bảo vệ môi trường, giảm rác thải và sáng tạo xanh.";
        
    // 2. Chuỗi DỮ LIỆU CỐT LÕI (BACKGROUND_DATA): Chứa tất cả thông tin tham khảo
    private static final String BACKGROUND_DATA = 
        "Sử dụng thông tin sau để trả lời câu hỏi của người dùng. **KHÔNG** đề cập đến chuỗi thông tin này trong câu trả lời.\n" +
        "--- DỮ LIỆU CỐT LÕI ---\n" +
        "## 🌱 1. Giới thiệu & thông tin chung\n" +
        "❓Giấy nảy mầm là gì? 👉 Là loại giấy có chứa hạt giống trong cấu trúc. Sau khi sử dụng, giấy có thể trồng xuống đất để hạt nảy mầm thành cây.\n" +
        "❓Giấy nảy mầm được làm từ nguyên liệu nào? 👉 Giấy được làm từ thân và cuống lá cây lục bình, kết hợp tinh bột và hạt giống hoa mười giờ.\n" +
        "❓Tại sao lại sử dụng cây lục bình để làm giấy? 👉 Vì lục bình phát triển nhanh, chứa nhiều cellulose, dễ tái chế và giúp giảm ô nhiễm sông rạch.\n" +
        "❓Giấy nảy mầm khác gì so với giấy thường? 👉 Giấy nảy mầm có thể trồng được, thân thiện môi trường và tự phân hủy sinh học.\n" +
        "❓Có thể viết hoặc in lên giấy nảy mầm không? 👉 Có thể, nhưng nên dùng mực tự nhiên, không chứa hóa chất gây hại cho hạt.\n\n" +
        "## 🪴 2. Cách sử dụng giấy nảy mầm\n" +
        "❓Cách sử dụng giấy nảy mầm như thế nào? 👉 Làm ẩm giấy, đặt vào chậu đất tơi xốp, phủ nhẹ một lớp đất mỏng và tưới nước đều hằng ngày.\n" +
        "❓Có cần ngâm nước giấy trước khi trồng không? 👉 Không cần, chỉ cần làm ẩm giấy vừa phải trước khi đặt xuống đất.\n" +
        "❓Giấy nên trồng trong đất gì để hạt dễ nảy mầm? 👉 Đất tơi xốp, có khả năng thoát nước tốt là phù hợp nhất.\n" +
        "❓Bao lâu thì hạt bắt đầu nảy mầm? 👉 Hạt hoa mười giờ thường nảy sau 5–7 ngày, tùy độ ẩm và ánh sáng.\n" +
        "❓Có thể trồng trong chậu nhỏ được không? 👉 Có, giấy rất phù hợp trồng trong chậu, ly hoặc khay nhỏ để làm quà tặng sinh thái.\n" +
        "❓Khi trồng xong giấy có cần phủ đất lên không? 👉 Nên phủ một lớp đất mỏng để giữ ẩm và giúp hạt tiếp xúc tốt với môi trường trồng.\n\n" +
        "## 🌼 3. Bảo quản & lưu ý\n" +
        "❓Giấy nảy mầm nên được bảo quản thế nào? 👉 Bảo quản trong nơi khô ráo, thoáng mát, tránh ánh nắng trực tiếp, nhiệt độ lý tưởng 20–28°C.\n" +
        "❓Giấy có thể để ngoài trời không? 👉 Không nên để lâu ngoài trời vì ẩm hoặc nắng gắt có thể làm hư hạt.\n" +
        "❓Nếu giấy bị ướt có còn trồng được không? 👉 Có thể, nhưng nên trồng ngay để tránh nấm mốc hoặc hư hạt.\n" +
        "❓Nếu giấy bị ẩm ướt lâu ngày thì cây có mọc lên không? 👉 Khó mọc, vì hạt có thể bị thối hoặc mất khả năng nảy mầm.\n" +
        "❓Thời hạn sử dụng của giấy nảy mầm là bao lâu? 👉 Khoảng 6–8 tháng kể từ ngày sản xuất nếu được bảo quản tốt, sau đó tỷ lệ nảy mầm sẽ giảm.\n" +
        "❓Có cần tránh ánh nắng trực tiếp không? 👉 Có, vì ánh nắng mạnh có thể làm khô và giảm độ nảy mầm của hạt.\n" +
        "❓Nếu hạt trong giấy không nảy mầm thì phải làm sao? 👉 Có thể do đất quá ướt, ánh sáng yếu hoặc bảo quản quá lâu — nên thử lại với điều kiện khô ráo và nắng nhẹ.\n\n" +
        "## 🌾 4. Loại hạt và ứng dụng\n" +
        "❓Giấy nảy mầm chứa hạt gì? 👉 Giấy hiện chứa hạt hoa mười giờ, dễ nảy mầm và sinh trưởng tốt.\n" +
        "❓Có thể chọn loại hạt riêng cho giấy không? 👉 Hiện nhóm nghiên cứu mới thử nghiệm thành công với hạt hoa mười giờ.\n" +
        "❓Giấy nảy mầm có trồng được rau, hoa không? 👉 Có thể, nếu dùng loại hạt phù hợp (hoa, rau mùi, cúc, hướng dương…).\n" +
        "❓Sau khi trồng, cây có phát triển bình thường không? 👉 Có, nếu đảm bảo đủ nước, ánh sáng và đất tơi xốp.\n" +
        "❓Có thể làm giấy nảy mầm làm quà tặng được không? 👉 Rất phù hợp, thường dùng trong thiệp cảm ơn, quà sinh thái, chiến dịch môi trường.\n" +
        "❓Có thể cắt giấy nảy mầm thành hình trang trí không? 👉 Có, nhưng cần tránh làm rách phần chứa hạt.\n" +
        "❓Giấy này phù hợp cho chiến dịch bảo vệ môi trường nào? 👉 Các chiến dịch “Trồng cây xanh”, “Giảm rác thải”, hoặc “Tái chế sáng tạo”.\n\n" +
        "## 🌍 5. Tác động môi trường & giáo dục\n" +
        "❓Giấy nảy mầm có thân thiện với môi trường không? 👉 Có, vì hoàn toàn phân hủy sinh học, không dùng hóa chất tẩy trắng, giúp giảm rác thải.\n" +
        "❓Làm giấy từ lục bình giúp giảm ô nhiễm như thế nào? 👉 Giúp tận dụng nguồn lục bình dày đặc trên sông, giảm tắc nghẽn dòng chảy và mùi hôi khi phân hủy.\n" +
        "❓Dự án này có giúp tái chế chất thải sinh học không? 👉 Có, vì lục bình là phụ phẩm tự nhiên, được tái chế thay vì bỏ đi.\n" +
        "❓Vì sao giấy nảy mầm lại quan trọng trong kinh tế xanh? 👉 Vì là sản phẩm tái chế sáng tạo, góp phần giảm thiểu rác thải và tạo giá trị kinh tế từ nguyên liệu tự nhiên.\n" +
        "❓Sản phẩm này có thể ứng dụng trong trường học ra sao? 👉 Có thể dùng trong dạy học STEM, hoạt động môi trường hoặc dự án khoa học của học sinh.\n\n" +
        "## 💡 6. Hỗ trợ người dùng\n" +
        "❓Tôi có thể tự làm giấy nảy mầm tại nhà không? 👉 Có thể, bằng cách trộn bột giấy thủ công với hạt giống, phơi khô tự nhiên.\n" +
        "❓Cần bao nhiêu nước để trồng giấy nảy mầm? 👉 Tưới nhẹ mỗi ngày để giữ ẩm, không đọng nước.\n" +
        "❓Tôi có thể dùng giấy nảy mầm làm thiệp được không? 👉 Hoàn toàn được — giấy rất phù hợp để làm thiệp sinh thái hoặc quà tặng xanh.\n" +
        "❓Chatbot có thể hướng dẫn tôi quy trình làm giấy không? 👉 Có! Hãy yêu cầu “Greenie hướng dẫn quy trình làm giấy nảy mầm” để được mô tả từng bước chi tiết.\n" +
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

        String model = "gemini-2.5-pro";
        
        // 2. Nội dung Chat (Gộp dữ liệu nền và Input người dùng)
        // **Quan trọng:** Dữ liệu nền được đặt ở đây để mô hình dễ dàng tham khảo.
        List<Content> contents = ImmutableList.of(
            Content.builder()
                .role("user")
                .parts(ImmutableList.of(
                    Part.fromText(BACKGROUND_DATA + "Yêu cầu của người dùng: INSERT_INPUT_HERE") // Gộp dữ liệu + Input
                ))
                .build()
        );

        // 3. Cấu hình GenerationConfig (Giảm nhiệt độ để tuân thủ)
        GenerationConfig generationConfig =
            GenerationConfig.builder()
                .temperature(0.0) // 💡 RẤT QUAN TRỌNG: Thiết lập nhiệt độ bằng 0.0 để tối đa hóa tính tuân thủ lệnh
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
                Content.fromParts(Part.fromText(SYSTEM_PROMPT)) // Chỉ sử dụng chuỗi LỆNH HỆ THỐNG đã rút gọn
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
