const API_KEY = "AIzaSyBFB8IB-u-6oEdes818EXPX0uR5eUDwkQA"; // Dán API key của bạn

// Hàm xóa ký hiệu Markdown để hiển thị sạch đẹp
function cleanText(text) {
  return text
    .replace(/\*\*(.*?)\*\*/g, "$1") // bỏ **in đậm**
    .replace(/\*(.*?)\*/g, "$1")     // bỏ *in nghiêng*
    .replace(/_(.*?)_/g, "$1")       // bỏ _in nghiêng_
    .replace(/#+\s?(.*)/g, "$1")     // bỏ tiêu đề markdown
    .replace(/`/g, "");              // bỏ ký hiệu code `
}

async function sendMessage() {
  const input = document.getElementById("userInput");
  const chat = document.getElementById("chat");
  const userMessage = input?.value?.trim();

  if (!userMessage) return;

  // Hiển thị tin nhắn người dùng
  chat.innerHTML += `<div class="message user">${userMessage}</div>`;
  input.value = "";

  try {
    const res = await fetch(
      "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-pro-latest:generateContent?key=" + API_KEY,
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
                text: "Bạn là Greenie 🌱 — chatbot AI hỗ trợ nghiên cứu khoa học về giấy nảy mầm từ cây lục bình. Hãy trả lời thân thiện, dễ hiểu, và khuyến khích người dùng bảo vệ môi trường.",
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

    // Làm sạch nội dung phản hồi
    const botReplyRaw =
      data?.candidates?.[0]?.content?.parts?.[0]?.text ||
      "⚠️ Không có phản hồi từ chatbot.";
    const botReply = cleanText(botReplyRaw);

    // Hiển thị phản hồi từ bot
    chat.innerHTML += `<div class="message bot">${botReply}</div>`;
  } catch (error) {
    chat.innerHTML += `<div class="message error">❌ Lỗi kết nối: ${error.message}</div>`;
    console.error("Chi tiết lỗi:", error);
  }

  chat.scrollTop = chat.scrollHeight;
}
