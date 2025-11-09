const API_KEY = "AIzaSyCiBzyvRsKREQsXNIZYjAoionJrV_S_wuA";
const MODEL = "gemini-2.5-flash"; // Dùng model ổn định và nhanh

async function sendMessage() {
  const input = document.getElementById("user-input");
  const chat = document.getElementById("chat");
  const userMessage = input.value.trim();
  if (!userMessage) return;

  // Dùng class user-msg để khớp với style.css (đã sửa ở các bước trước)
  chat.innerHTML += `<div class="message user-msg">${userMessage}</div>`; 
  input.value = "";

  chat.scrollTop = chat.scrollHeight;

  try {
    const res = await fetch(
      `https://generativelanguage.googleapis.com/v1/models/${MODEL}:generateContent?key=${API_KEY}`,
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          // 🚨 SỬA LỖI CUỐI CÙNG: Loại bỏ hoàn toàn trường system_instruction
          // và đặt hướng dẫn vào phần contents, dùng vai trò "user" cho hướng dẫn
          // để khắc phục lỗi "Unknown name system_instruction".
          contents: [
            {
              role: "user",
              parts: [
                {
                  // Hướng dẫn hệ thống được thêm vào đầu tiên
                  text: "Bạn là Greenie 🌱 — chatbot hỗ trợ nghiên cứu khoa học về giấy nảy mầm từ cây lục bình. Hãy trả lời thân thiện, rõ ràng, không dùng dấu *. Hướng dẫn này thay cho System Instruction."
                }
              ]
            },
            { 
                role: "user", // Tin nhắn thực tế của người dùng
                parts: [{ text: userMessage }] 
            }
          ],
          generationConfig: { temperature: 0.7, topP: 0.9 }
        }),
      }
    );

    const data = await res.json();

    // Kiểm tra lỗi 503
    if (data.error?.code === 503) {
      chat.innerHTML += `<div class="message error">⚠️ Máy chủ quá tải. Đang thử lại sau 5 giây...</div>`;
      console.warn("Máy chủ quá tải, thử lại sau...");
      setTimeout(sendMessage, 5000);
      return;
    }

    if (data.error) {
      chat.innerHTML += `<div class="message error">❌ Lỗi API: ${data.error.message}</div>`;
      console.error("Chi tiết lỗi:", data.error);
      return;
    }

    const botReply =
      data?.candidates?.[0]?.content?.parts?.[0]?.text ||
      "⚠️ Không có phản hồi từ chatbot.";
    
    // SỬA CLASS CSS: Dùng bot-msg để khớp với style.css
    chat.innerHTML += `<div class="message bot-msg">${botReply}</div>`; 
  } catch (error) {
    chat.innerHTML += `<div class="message error">❌ Lỗi kết nối: ${error.message}</div>`;
    console.error("Chi tiết lỗi:", error);
  }

  chat.scrollTop = chat.scrollHeight;
}

// ----------------------------------------------------
// KHẮC PHỤC LỖI KHÔNG BẤM GỬI ĐƯỢC: Đảm bảo hai đoạn này có ở cuối file

// Gắn sự kiện nút "Gửi"
document.getElementById("sendBtn").addEventListener("click", sendMessage);

// Thêm hỗ trợ phím Enter
document.getElementById("user-input").addEventListener("keypress", (e) => {
  if (e.key === 'Enter') {
    e.preventDefault(); 
    sendMessage();
  }
});
