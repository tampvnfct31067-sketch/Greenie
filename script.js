const API_KEY = "AIzaSyCiBzyvRsKREQsXNIZYjAoionJrV_S_wuA";
const MODEL = "gemini-2.5-flash"; // Dùng model nhanh và ổn định

// ** Hướng dẫn hệ thống (System Instruction) được đưa vào biến riêng **
const SYSTEM_PROMPT = "Bạn là Greenie 🌱 — chatbot hỗ trợ nghiên cứu khoa học về giấy nảy mầm từ cây lục bình. Hãy trả lời thân thiện, rõ ràng, không dùng dấu *.";

async function sendMessage() {
  const input = document.getElementById("user-input");
  const chat = document.getElementById("chat");
  const userMessage = input.value.trim();
  if (!userMessage) return;

  // Gắn tin nhắn người dùng (Sử dụng class user-msg để khớp CSS)
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
          // Cấu trúc JSON đơn giản nhất để tránh lỗi cú pháp
          contents: [
            {
              role: "user",
              parts: [{ text: SYSTEM_PROMPT + "\n\n" + userMessage }] // Gộp hướng dẫn vào tin nhắn đầu tiên
            }
          ]
        }),
      }
    );

    const data = await res.json();

    if (data.error) {
      // Sửa lỗi class CSS
      chat.innerHTML += `<div class="message error">❌ Lỗi API: ${data.error.message}</div>`;
      console.error("Chi tiết lỗi:", data.error);
      return;
    }

    const botReply =
      data?.candidates?.[0]?.content?.parts?.[0]?.text ||
      "⚠️ Không có phản hồi từ chatbot.";
    
    // Sửa lỗi class CSS
    chat.innerHTML += `<div class="message bot-msg">${botReply}</div>`; 
  } catch (error) {
    chat.innerHTML += `<div class="message error">❌ Lỗi kết nối: ${error.message}</div>`;
    console.error("Chi tiết lỗi:", error);
  }

  chat.scrollTop = chat.scrollHeight;
}

// ----------------------------------------------------
// Đảm bảo phần gắn sự kiện này nằm ở cuối file để DOM đã sẵn sàng

// Gắn sự kiện nút "Gửi"
document.getElementById("sendBtn").addEventListener("click", sendMessage);

// Thêm hỗ trợ phím Enter
document.getElementById("user-input").addEventListener("keypress", (e) => {
  if (e.key === 'Enter') {
    e.preventDefault(); 
    sendMessage();
  }
});
