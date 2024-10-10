#define HIGHP

uniform sampler2D u_texture;
uniform sampler2D u_noise;
uniform sampler2D u_distortmap;

uniform vec2 u_campos;
uniform vec2 u_resolution;
uniform float u_time;

uniform float u_ww; //world width
uniform float u_wh; //      height

uniform float u_opacity;

varying vec2 v_texCoords;

#define NSCALE 170.0 / 2.0
#define DSCALE 160.0 / 2.0

void heatmap() {
    vec2 c = v_texCoords;
    vec2 v = vec2(1.0/u_resolution.x, 1.0/u_resolution.y);
    vec2 coords = vec2(c.x / v.x + u_campos.x, c.y / v.y + u_campos.y);

    float atime = u_time / 15000.0;
    float noise = (texture2D(u_noise, (coords) / DSCALE + vec2(atime) * vec2(-0.9, 0.8)).r + texture2D(u_noise, (coords) / DSCALE + vec2(atime * 1.1) * vec2(0.8, -1.0)).r) / 2.0;
    noise = abs(noise - 0.5) * 7.0 + 0.23;

    vec2 worldc = vec2(coords.x,coords.y);

    float heatAmount = clamp(texture2D(u_distortmap, vec2(worldc.x / u_ww, worldc.y / u_wh)).r,0.0,1.0);
    float coolAmount = clamp(texture2D(u_distortmap, vec2(worldc.x / u_ww, worldc.y / u_wh)).b,0.0,1.0);
    gl_FragColor = vec4(heatAmount*noise,min(1.0-heatAmount,1.0-coolAmount)*noise,coolAmount*noise,1.0);
}
void main() {
    vec2 c = v_texCoords;
    vec2 v = vec2(1.0/u_resolution.x, 1.0/u_resolution.y);
    vec2 coords = vec2(c.x / v.x + u_campos.x, c.y / v.y + u_campos.y);

    float atime = u_time / 15000.0;
    float noise = (texture2D(u_noise, (coords) / DSCALE + vec2(atime) * vec2(-0.9, 0.8)).r + texture2D(u_noise, (coords) / DSCALE + vec2(atime * 1.1) * vec2(0.8, -1.0)).r) / 2.0;
    noise = abs(noise - 0.5) * 7.0 + 0.23;

    vec2 worldc = vec2(coords.x,coords.y);

    vec4 orig = texture2D(u_texture, c);
    gl_FragColor = orig; // in case something breaks

    float stime = u_time / 5.0;
    float distortAmount = clamp(texture2D(u_distortmap, vec2(worldc.x / u_ww+4.0/u_wh, worldc.y / u_wh+4.0/u_wh)).r,0.0,1.0);
    if(!(0 <= worldc.x && worldc.x < u_ww && 0 <= worldc.y && worldc.y < u_wh)) distortAmount = 0;
    vec2 distortOffset = vec2(sin(stime/3.0+coords.y/8.0)*v.x*0.25, sin(stime/2.5+coords.x/8.0)*v.y*0.5);
    vec2 distortOffset2 = vec2(sin(stime/3.0+coords.y/8.0+distortAmount*1.5)*v.x*0.25, sin(stime/2.5+coords.x/8.0+distortAmount*1.5)*v.y*0.5);
    distortOffset *= noise;
    vec4 sampled = texture2D(u_texture, vec2(c.x + (distortOffset.x)*distortAmount, c.y + (distortOffset.y)*distortAmount));
    vec4 sampled2 = texture2D(u_texture, vec2(c.x + (distortOffset2.x)*distortAmount, c.y + (distortOffset2.y)*distortAmount));
    gl_FragColor = vec4(sampled.r-0.2*distortAmount*u_opacity,sampled.g-0.2*distortAmount*u_opacity,sampled.b+0.05*distortAmount*u_opacity,sampled.a);

    float noise2 = (texture2D(u_noise, (coords) / DSCALE/4 + vec2(atime) * vec2(-0.9, 0.8)).r + texture2D(u_noise, (coords) / DSCALE/4 + vec2(atime * 1.1) * vec2(0.8, -1.0)).r) / 2.0;
    noise2 = abs(noise2 - 0.5) * 7.0 + 0.23;
    distortAmount *= noise2;
    if(distortAmount<0.2 && distortAmount > 0) {
        float dist = 1-abs(distortAmount-0.1)/0.1;
        float eff = dist*0.5;
        gl_FragColor = lerp(gl_FragColor,vec4(0.5,0.7,1.0,1.0),eff*u_opacity);
    }
    distortAmount /= noise2;

    float noise3 = (texture2D(u_noise, (coords) / DSCALE / 16 + vec2(atime,sin(atime)) * vec2(-0.9, 0.8)).r + texture2D(u_noise, (coords) / DSCALE / 16 + vec2(atime * 2,cos(atime)) * vec2(0.8, -1.0)).r) / 2.0;
    noise3 = abs(noise3 - 0.5) * 7.0 + 0.23;
    distortAmount *= noise3;

    float noise4 = (texture2D(u_noise, (coords) / DSCALE / 8 + vec2(atime,sin(atime)) * vec2(-0.9, 0.8)).r + texture2D(u_noise, (coords) / DSCALE / 8 + vec2(atime * 2,cos(-atime)) * vec2(0.8, -1.0)).r) / 2.0;
    noise4 = abs(noise4 - 0.5) * 3.0 + 0.23;

    distortAmount /= noise4;

    if(distortAmount>0.2) {
        gl_FragColor = lerp(gl_FragColor,vec4(0.5,0.7,1.0,1.0),distortAmount/4*u_opacity);
    }
    //heatmap();
}

vec4 lerp(vec4 a, vec4 b, float t) {
    return a + t * (b - a);
}