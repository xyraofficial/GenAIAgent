const axios = require('axios');

module.exports = async (req, res) => {
  if (req.method !== 'POST') {
    return res.status(405).json({ error: 'Method Not Allowed' });
  }

  const { message, persona, web_search } = req.body;
  const apiKey = process.env.OPENAI_API_KEY;

  let systemPrompt = "You are a helpful AI assistant.";
  if (persona === "Friendly Assistant") systemPrompt = "You are a friendly and cheerful AI assistant. Use emojis and be very supportive.";
  if (persona === "Professional Expert") systemPrompt = "You are a professional expert AI. Give concise, accurate, and formal answers.";
  if (persona === "Coding Mentor") systemPrompt = "You are a coding mentor. Explain concepts clearly, provide code examples, and help the user learn.";

  if (web_search) {
      systemPrompt += " You have access to the internet. If the user asks about current events, acknowledge that you are searching and provide the most up-to-date information possible based on your knowledge cutoff or simulate a search result.";
  }

  try {
    const response = await axios.post('https://api.openai.com/v1/chat/completions', {
      model: 'gpt-4o-mini',
      messages: [
          { role: 'system', content: systemPrompt },
          { role: 'user', content: message }
      ]
    }, {
      headers: {
          'Authorization': `Bearer ${apiKey}`,
          'Content-Type': 'application/json'
      }
    });

    res.status(200).json(response.data);
  } catch (error) {
    console.error(error);
    res.status(500).json({ error: 'Failed to fetch from OpenAI' });
  }
};