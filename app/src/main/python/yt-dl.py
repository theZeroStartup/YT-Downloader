import yt_dlp as yt_dlp

def getVideoDetails(video_url, download=False):
    with yt_dlp.YoutubeDL(ydl_video_opts) as ydl:
        try:
            info = ydl.extract_info(video_url, download=download)
            return info
        except:
            print('Not working..')
            return None

ydl_video_opts = {
    'format': 'bv/bestvideo',  # Best available video format (no audio)
    'n_threads': 10,
    'http_chunk_size': "5M",
    'geo_bypass': True,
    'force_ipv4': True,
    'verbose': True,
    'noplaylist': True,
    'quiet': True,  # Suppresses unnecessary output
}