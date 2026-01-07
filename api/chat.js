const axios = require('axios');

module.exports = async (req, res) => {
  if (req.method !== 'POST') {
    return res.status(405).json({ error: 'Method Not Allowed' });
  }

  const { message, persona, web_search } = req.body;
  const apiKey = process.env.OPENROUTER_API_KEY;

  let systemPrompt = "You are a helpful AI assistant.";
  if (persona === "Friendly Assistant") systemPrompt = "You are a friendly and cheerful AI assistant. Use emojis and be very supportive.";
  if (persona === "Professional Expert") systemPrompt = "You are a professional expert AI. Give concise, accurate, and formal answers.";
  if (persona === "Coding Mentor") systemPrompt = "You are a coding mentor. Explain concepts clearly, provide code examples, and help the user learn.";

  if (web_search) {
    systemPrompt += " You have access to real-time information. Use your internal browsing capabilities to provide the most recent data available for the user's query.";
  }

  try {
    if (!apiKey) {
      return res.status(500).json({ error: 'OpenRouter API Key is not configured in environment variables' });
    }

    const response = await axios.post('https://openrouter.ai/api/v1/chat/completions', {
      model: 'deepseek/deepseek-r1:free', // Using DeepSeek R1 free model via OpenRouter
      messages: [
        { role: 'system', content: systemPrompt },
        { role: 'user', content: message }
      ],
      // OpenRouter requirements
      headers: {
        "HTTP-Referer": "https://replit.com",
        "X-Title": "GenAI Agent"
      }
    }, {
      headers: {
        'Authorization': `Bearer ${apiKey}`,
        'Content-Type': 'application/json'
      },
      timeout: 30000
    });

    if (response.data && response.data.choices && response.data.choices.length > 0) {
      res.status(200).json(response.data);
    } else {
      res.status(500).json({ error: 'Empty response from OpenRouter' });
    }
  } catch (error) {
    console.error('OpenRouter Error:', error.response ? error.response.data : error.message);
    const statusCode = error.response ? error.response.status : 500;
    res.status(statusCode).json({ 
      error: 'Failed to fetch from OpenRouter', 
      details: error.response ? error.response.data : error.message 
    });
  }
};