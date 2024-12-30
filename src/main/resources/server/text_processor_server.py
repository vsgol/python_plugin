from flask import Flask, request, jsonify
import re

app = Flask(__name__)

def get_word_lengths(text):
    # Using regex to split words
    words = re.findall(r'\b\w+\b', text)
    result = {word: len(word) for word in words}  # Store word lengths in a dictionary
    return result

@app.route('/process-text', methods=['POST'])
def process_text():
    data = request.json
    if not data or 'text' not in data:
        return jsonify({'error': 'No text provided'}), 400

    text = data['text']
    processed_text = get_word_lengths(text)
    return jsonify({'processed_text': "\n".join([f"{i[0]}: {i[1]}" for i in processed_text.items()])})

if __name__ == '__main__':
    app.run(host='127.0.0.1', port=5000)  # Adjust the port as needed
