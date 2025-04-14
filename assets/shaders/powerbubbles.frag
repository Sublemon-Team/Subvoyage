#define HIGHP

uniform sampler2D u_texture;
uniform sampler2D u_noise;

uniform vec2 u_campos;
uniform vec2 u_resolution;
uniform float u_time;

varying vec2 v_texCoords;
#define NSCALE 190.0 / 2.0
#define DSCALE 170.0 / 2.0
const float mscl = 35.0;
const float mth = 7.0;
#define step 2.0
void main(){

    vec2 c = v_texCoords;
    vec2 v = vec2(1.0/u_resolution.x, 1.0/u_resolution.y);
    vec2 coords = vec2(c.x / v.x + u_campos.x, c.y / v.y + u_campos.y);

    float stime = u_time / 5.0;

    vec4 sampled = texture2D(u_texture, c);
    vec3 color = sampled.rgb * vec3(0.9, 0.9, 1.0);
    vec4 noiseSample = texture2D(u_noise,coords / DSCALE * (-1.0,1.0) + stime / 1024.0);
    vec4 noiseSample2 = texture2D(u_noise,coords / NSCALE * (1.0,-1.0) + stime / 1024.0);

    float noi = (noiseSample.r + noiseSample2.r)/2.0;

    float tester = mod((coords.x + coords.y*1.4 + sin(stime / 8.0 + coords.x/5.0 - coords.y/20.0)*2.0) +
    sin(stime / 20.0 + coords.y/3.0) * 1.0 * noi +
    sin(stime / 10.0 - coords.y/2.0) * 4.0 * noi +
    sin(stime / 7.0 + coords.y/1.0) * 0.5 +
    sin(coords.x / 3.0 + coords.y / 2.0) +
    sin(stime / 20.0 + coords.x/4.0) * 1.0, mscl) + noi * 6.0;


    vec4 maxed = max(max(max(texture2D(u_texture, c + vec2(0.0, step) * v), texture2D(u_texture, c + vec2(0.0, -step) * v)), texture2D(u_texture, c + vec2(step, 0.0) * v)), texture2D(u_texture, c + vec2(-step, 0.0) * v));
    float mmm = 1.0;
    if(tester < mth || (0.45 > noi && noi > 0.43) || (0.55 > noi && noi > 0.53) || (0.35 > noi && noi > 0.33)){
        color *= 1.2;
        mmm *= 2.0;
    }
    /*if(tester < mth && (0.45 > noi && noi > 0.43)){
        color *= 1.5;
        mmm *= 1.5;
    }*/
    if(texture2D(u_texture, c).a < 0.9 && maxed.a > 0.9){
        gl_FragColor = vec4(maxed.rgb, maxed.a * 100.0);
    }
    else {
        gl_FragColor = vec4(color.rgb, 0.2*mmm*sampled.a);
    }
}
