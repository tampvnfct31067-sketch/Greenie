const API_KEY = "AIzaSyCiBzyvRsKREQsXNIZYjAoionJrV_S_wuA";
const MODEL = "gemini-2.0-pro-exp-02-05"; // hoặc "gemini-1.5-pro" nếu bạn có quyền

async function sendMessage() {
  const input = document.getElementById("userInput");
  const chat = document.getElementById("chat");
  const userMessage = input.value.trim();
  if (!userMessage) return;

  // Hiển thị tin nhắn người dùng
  chat.innerHTML += `<div class="message user">${userMessage}</div>`;
  input.value = "";

  try {
    const res = await fetch(
      `https://generativelanguage.googleapis.com/v1beta/models/${MODEL}:generateContent?key=${API_KEY}`,
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          contents: [
            {
              role: "user",
              parts: [{ text: userMessage }],
            },
          ],
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
      chat.innerHTML += `<div class="message error">❌ Lỗi API: ${data.error.message}</div>`;
      console.error("Chi tiết lỗi:", data.error);
      return;
    }

    const botReply =
      data?.candidates?.[0]?.content?.parts?.[0]?.text ||
      "⚠️ Không có phản hồi từ chatbot.";
    chat.innerHTML += `<div class="message bot">${botReply}</div>`;
  } catch (error) {
    chat.innerHTML += `<div class="message error">❌ Lỗi kết nối: ${error.message}</div>`;
    console.error("Chi tiết lỗi:", error);
  }

  chat.scrollTop = chat.scrollHeight;
}
