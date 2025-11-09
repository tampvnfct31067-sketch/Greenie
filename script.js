const API_KEY = "AIzaSyCiBzyvRsKREQsXNIZYjAoionJrV_S_wuA"; // 🔑 API key mới
const MODEL = "gemini-1.5-flash-latest"; // ⚡ model nhẹ, ít lỗi quota

async function sendMessage() {
  const input = document.getElementById("userInput");
  const chat = document.getElementById("chat");
  const userMessage = input?.value.trim();
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
              parts: [{ text: userMessage }]
            }
          ],
          system_instruction: {
            role: "system",
            parts: [
              {
                text: "Bạn là Greenie — chatbot AI nghiên cứu khoa học về giấy nảy mầm từ lục bình 🌱. Hãy trả lời thân thiện, dễ hiểu và ngắn gọn."
              }
            ]
          }
        }),
      }
    );

    // Xử lý phản hồi
    const data = await res.json();

    if (data.error) {
      chat.innerHTML += `<div class="message error">❌ Lỗi API: ${data.error.message}</div>`;
      console.error("Chi tiết lỗi:", data.error);
      return;
    }

    // Làm sạch Markdown để không có dấu **, *...
    const cleanText = (text) => {
      return text
        .replace(/\*\*(.*?)\*\*/g, '$1')
        .replace(/\*(.*?)\*/g, '$1')
        .replace(/_(.*?)_/g, '$1')
        .replace(/#+\s?(.*)/g, '$1');
    };

    const botReplyRaw =
      data?.candidates?.[0]?.content?.parts?.[0]?.text ||
      "⚠️ Không có phản hồi từ chatbot.";
    const botReply = cleanText(botReplyRaw);

    chat.innerHTML += `<div class="message bot">${botReply}</div>`;
  } catch (error) {
    chat.innerHTML += `<div class="message error">❌ Lỗi kết nối: ${error.message}</div>`;
    console.error("Chi tiết lỗi:", error);
  }

  chat.scrollTop = chat.scrollHeight;
}
