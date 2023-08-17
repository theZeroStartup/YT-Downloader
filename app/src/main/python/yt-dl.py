import yt_dlp as yt_dlp

def client(video_url, download=False):
    with yt_dlp.YoutubeDL(ydl_opts) as ydl:
        try:
            info = ydl.extract_info(video_url, download=download)
            return info
        except:
            print('Not working..')
            return None

ydl_opts = {
        # Parameters ensure that the high-quality audio tracks are returned as priority #1.
        # However, we'll not be using those, as we're looking for best quality video + audio
        'format': 'bestaudio/best',
        'postprocessors': [{
            'key': 'FFmpegExtractAudio',
            'preferredcodec': 'mp3',
            'preferredquality': '192',
        }],
        'noplaylist': True,
}