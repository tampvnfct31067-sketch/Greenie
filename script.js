const API_KEY = "AIzaSyCiBzyvRsKREQsXNIZYjAoionJrV_S_wuA";
const MODEL = "gemini-2.5-flash"; // Nên dùng 2.5-flash thay vì 2.0-pro-exp-02-05

async function sendMessage() {
  const input = document.getElementById("user-input");
  const chat = document.getElementById("chat");
  const userMessage = input.value.trim();
  if (!userMessage) return;

  // SỬA CLASS CSS: Dùng user-msg để khớp với style.css
  chat.innerHTML += `<div class="message user-msg">${userMessage}</div>`; 
  input.value = "";

  // Tự động cuộn xuống
  chat.scrollTop = chat.scrollHeight;

  try {
    const res = await fetch(
      // Cập nhật URL để dùng MODEL mới (2.5-flash) nếu cần
      `https://generativelanguage.googleapis.com/v1/models/${MODEL}:generateContent?key=${API_KEY}`,
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          contents: [{ role: "user", parts: [{ text: userMessage }] }],
          generationConfig: { temperature: 0.7, topP: 0.9 },
          // THÊM LẠI SYSTEM INSTRUCTION để đặt vai trò cho chatbot
          system_instruction: "Bạn là Greenie 🌱 — chatbot hỗ trợ nghiên cứu khoa học về giấy nảy mầm từ cây lục bình. Hãy trả lời thân thiện, rõ ràng, không dùng dấu *.",
        }),
      }
    );

    const data = await res.json();

    // Kiểm tra lỗi 503 và tự động thử lại
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

// 🛑 KHẮC PHỤC LỖI KHÔNG BẤM GỬI ĐƯỢC: THIẾU SỰ KIỆN NÀY!
document.getElementById("sendBtn").addEventListener("click", sendMessage);

// Thêm hỗ trợ phím Enter
document.getElementById("user-input").addEventListener("keypress", (e) => {
  if (e.key === 'Enter') {
    e.preventDefault(); 
    sendMessage();
  }
});
