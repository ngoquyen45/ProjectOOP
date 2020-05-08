FROM node
ADD webview webview
WORKDIR webview/
RUN npm install -g http-server
CMD http-server --port 443 -S -C cert.pem
EXPOSE 443
