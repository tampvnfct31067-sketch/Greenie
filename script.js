const API_KEY = "AIzaSyCiBzyvRsKREQsXNIZYjAoionJrV_S_wuA";
const MODEL = "gemini-2.0-pro-exp-02-05";

async function sendMessage() {
  // SỬA: Đã sửa id trong index.html thành user-input để khớp với CSS
  const input = document.getElementById("user-input"); 
  const chat = document.getElementById("chat");
  const userMessage = input.value.trim();
  if (!userMessage) return;

  // 📌 SỬA: Dùng class .user-msg thay cho .message user để khớp với style.css
  chat.innerHTML += `<div class="message user-msg">${userMessage}</div>`; 
  input.value = "";

  try {
    const res = await fetch(
      `https://generativelanguage.googleapis.com/v1/models/${MODEL}:generateContent?key=${API_KEY}`,
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          contents: [{ role: "user", parts: [{ text: userMessage }] }],
          system_instruction: {
            role: "system",
            parts: [
              {
                text: "Bạn là Greenie 🌱 — chatbot hỗ trợ nghiên cứu khoa học về giấy nảy mầm từ cây lục bình. Hãy trả lời thân thiện, rõ ràng, không dùng dấu *.",
              },
            ],
          },
        }),
      }
    );

    const data = await res.json();

    if (data.error) {
      // 📌 SỬA: Dùng class .error để khớp với style.css
      chat.innerHTML += `<div class="message error">❌ Lỗi API: ${data.error.message}</div>`;
      console.error("Chi tiết lỗi:", data.error);
      return;
    }

    const botReply =
      data?.candidates?.[0]?.content?.parts?.[0]?.text ||
      "⚠️ Không có phản hồi từ chatbot.";
    // 📌 SỬA: Dùng class .bot-msg thay cho .message bot để khớp với style.css
    chat.innerHTML += `<div class="message bot-msg">${botReply}</div>`; 
  } catch (error) {
    // 📌 SỬA: Dùng class .error để khớp với style.css
    chat.innerHTML += `<div class="message error">❌ Lỗi kết nối: ${error.message}</div>`;
    console.error("Chi tiết lỗi:", error);
  }

  chat.scrollTop = chat.scrollHeight;
}

// Gắn sự kiện nút gửi: ID "sendBtn" đã đúng và không cần sửa.
document.getElementById("sendBtn").addEventListener("click", sendMessage);

// 📌 THÊM: Gắn sự kiện nhấn Enter để gửi tin nhắn (trải nghiệm tốt hơn)
document.getElementById("user-input").addEventListener("keypress", (e) => {
  if (e.key === 'Enter') {
    sendMessage();
  }
});
