const API_KEY = "AIzaSyCiBzyvRsKREQsXNIZYjAoionJrV_S_wuA";
const MODEL = "gemini-2.0-pro-exp-02-05"; // Hoặc gemini-2.5-flash

async function sendMessage() {
  const input = document.getElementById("user-input"); // Đảm bảo ID này khớp với HTML
  const chat = document.getElementById("chat");
  const userMessage = input.value.trim();
  if (!userMessage) return;

  chat.innerHTML += `<div class="message user-msg">${userMessage}</div>`;
  input.value = "";

  try {
    const res = await fetch(
      `https://generativelanguage.googleapis.com/v1/models/${MODEL}:generateContent?key=${API_KEY}`,
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          // 🚨 Đã sửa: Thay vì dùng trường system_instruction, 
          // ta đưa hướng dẫn hệ thống vào mảng contents với role: "system"
          contents: [
            { 
                role: "system", 
                parts: [
                    { 
                        // Nội dung hướng dẫn hệ thống
                        text: "Bạn là Greenie 🌱 — chatbot hỗ trợ nghiên cứu khoa học về giấy nảy mầm từ cây lục bình. Hãy trả lời thân thiện, rõ ràng, không dùng dấu *."
                    }
                ]
            },
            // Tin nhắn của người dùng sau đó
            { role: "user", parts: [{ text: userMessage }] }
          ],
        }),
      }
    );

    const data = await res.json();

    if (data.error) {
      chat.innerHTML += `<div class="message error">❌ Lỗi API: ${data.error.message}</div>`;
      console.error("Chi tiết lỗi:", data.error);
      return;
    }

    const botReply =
      data?.candidates?.[0]?.content?.parts?.[0]?.text ||
      "⚠️ Không có phản hồi từ chatbot.";
    chat.innerHTML += `<div class="message bot-msg">${botReply}</div>`; 
  } catch (error) {
    chat.innerHTML += `<div class="message error">❌ Lỗi kết nối: ${error.message}</div>`;
    console.error("Chi tiết lỗi:", error);
  }

  chat.scrollTop = chat.scrollHeight;
}

// Gắn sự kiện nút gửi
document.getElementById("sendBtn").addEventListener("click", sendMessage);

// Gắn sự kiện nhấn Enter
document.getElementById("user-input").addEventListener("keypress", (e) => {
  if (e.key === 'Enter') {
    sendMessage();
  }
});
