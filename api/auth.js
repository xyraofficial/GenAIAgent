const axios = require('axios');

module.exports = async (req, res) => {
  const { path } = req.query;
  const supabaseUrl = process.env.SUPABASE_URL;
  const supabaseKey = process.env.SUPABASE_ANON_KEY;

  try {
    const response = await axios({
      method: req.method,
      url: `${supabaseUrl}/auth/v1/${path}`,
      params: req.query, // Meneruskan grant_type=password
      data: req.body,
      headers: {
        'apikey': supabaseKey,
        'Authorization': `Bearer ${supabaseKey}`,
        'Content-Type': 'application/json'
      }
    });

    res.status(response.status).json(response.data);
  } catch (error) {
    console.error(error);
    res.status(error.response?.status || 500).json(error.response?.data || { error: 'Internal Server Error' });
  }
};