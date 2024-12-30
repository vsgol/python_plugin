from flask import Flask, request, jsonify

app = Flask(__name__)

@app.route('/process-text', methods=['POST'])
def process_text():
    data = request.json
    if not data or 'text' not in data:
        return jsonify({'error': 'No text provided'}), 400

    text = data['text']
    processed_text = text[::-1]  # Reverse the text
    return jsonify({'processed_text': processed_text})

if __name__ == '__main__':
    app.run(host='127.0.0.1', port=5000)  # Adjust the port as needed
